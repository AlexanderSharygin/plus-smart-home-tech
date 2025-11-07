package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.RequiredArgsConstructor;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final KafkaTopicConfig topicsConfig;
    private final KafkaConsumerSnapshotsConfig consumerConfig;
    private final SnapshotHandler handler;

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(topicsConfig.getSnapshots());
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer
                        .poll(Duration.ofMillis(consumerConfig.getConsumeAttemptTimeoutMs()));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    handler.buildSnapshot(snapshot);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке snapshots", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}