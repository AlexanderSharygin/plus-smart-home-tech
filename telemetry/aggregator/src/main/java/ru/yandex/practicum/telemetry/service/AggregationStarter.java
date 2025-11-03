package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.config.KafkaTopicConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.io.Serial;
import java.time.Duration;
import java.util.Optional;

@Setter
@RequiredArgsConstructor
@Slf4j
@Service
public class AggregationStarter {


    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final SnapshotService snapshotService;
    private final KafkaTopicConfig topics;

    public void start() {
        log.debug("Starting aggregation process.");
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            consumer.subscribe(topics.getConsumerSubscriptions());
            log.info("Subscribed for the topic: {}", topics.getConsumerSubscriptions());

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));
                if (!records.isEmpty()) {

                    for (ConsumerRecord<String, SensorEventAvro> record : records) {
                        processRecord(record);
                    }
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.warn("WakeupException caught - Consumer shutting down.");
        } catch (Exception e) {
            log.error("Error during event processing", e);
        } finally {
            cleanupResources();
        }

    }


    private void processRecord(final ConsumerRecord<String, SensorEventAvro> record) {
        log.info(
                "Processing  ConsumerRecord: topic={}, partition={}, offset={}, hubId={}, timestamp={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.timestamp());

        final Optional<SensorsSnapshotAvro> updatedSnapshot = snapshotService.updateState(
                record.value());
        updatedSnapshot.ifPresent(this::sendSnapshotToKafka);
    }


    private void sendSnapshotToKafka(final SensorsSnapshotAvro snapshot) {
        log.info("Sending snapshot to Kafka: hubId={}", snapshot.getHubId());

        final ProducerRecord<String, SensorsSnapshotAvro> record =
                new ProducerRecord<>(topics.getProducerTopic(), snapshot.getHubId(), snapshot);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send snapshot to Kafka", exception);
            } else {
                log.info("Snapshot sent successfully to the topic {} at offset {}", metadata.topic(),
                        metadata.offset());
            }
        });
    }


    private void cleanupResources() {
        try {
            log.info("Flushing producer buffer.");
            producer.flush();

            log.info("Committing consumer offsets synchronously.");
            consumer.commitSync();
        } catch (Exception e) {
            log.error("Error during resource cleanup", e);
        } finally {
            log.info("Closing Kafka consumer.");
            consumer.close();
            log.info("Closing Kafka producer.");
            producer.close();
        }
    }
}