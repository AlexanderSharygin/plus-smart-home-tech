package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConsumerSnapshotsConfig;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaTopicConfig;
import ru.yandex.practicum.telemetry.analyzer.handler.snapshot.SnapshotHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class SnapshotProcessor implements Runnable {
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final List<String> topics;
    private final Duration CONSUME_ATTEMPT_TIMEOUT;
    private final SnapshotHandler handler;

    public SnapshotProcessor(KafkaConsumer<String, SensorsSnapshotAvro> consumer,
                             KafkaTopicConfig topicsConfig,
                             KafkaConsumerSnapshotsConfig consumerConfig,
                             SnapshotHandler handler) {
        this.consumer = consumer;
        this.topics = topicsConfig.getSnapshots();
        this.CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(consumerConfig.getConsumeAttemptTimeoutMs());
        this.handler = handler;
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            log.info("Запуск обработчика снапшотов для топика: {}", topics.toString());
            consumer.subscribe(topics);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал завершения работы.");
                consumer.wakeup();
            }));

            while (true) {
                log.trace("Ожидание новых снапшотов...");
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                if (!records.isEmpty()) {
                    log.info("Получено {} снапшотов для обработки", records.count());
                }

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();

                    log.info("Получен снапшот: {}", snapshot);
                    handler.buildSnapshot(snapshot);

                    log.info("Снапшот для hubId: {} успешно обработан",
                            snapshot.getHubId());
                }
                log.debug("Фиксация смещений для обработанных снапшотов");
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Произошла ошибка в цикле обработки снапшотов для топика {}", topics.toString(), e);
        } finally {
            try {
                consumer.commitSync();
                log.info("Смещения зафиксированы перед закрытие консьюмера");
            } finally {
                consumer.close();
                log.info("Консьюмер закрыт");
            }
        }
    }
}