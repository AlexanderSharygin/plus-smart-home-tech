package pro.java.education.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import pro.java.education.aggregator.config.KafkaTopicConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;

@Setter
@RequiredArgsConstructor
@Slf4j
public class AggregationStarter {

    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final SnapshotService snapshotService;
    private final KafkaTopicConfig topics;


    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(topics.getConsumerSubscriptions());

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    snapshotService.updateState(record.value()).ifPresent(this::sendSnapshot);
                }
                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка во время освобождения ресурсов", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SensorsSnapshotAvro> record =
                new ProducerRecord<>(topics.getProducerTopic(), snapshot.getHubId(), snapshot);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка во время отправки сообщения в топик + {}", topics.producerTopic());
            }
        });
    }
}