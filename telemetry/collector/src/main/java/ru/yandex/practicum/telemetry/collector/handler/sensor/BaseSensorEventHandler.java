package ru.yandex.practicum.telemetry.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.config.KafkaTopic;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

import java.time.Instant;


@Slf4j
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final KafkaEventProducer producer;
    protected final String topic;
    private final KafkaConfig kafkaConfig;

    @Autowired
    protected BaseSensorEventHandler(KafkaEventProducer producer, KafkaConfig kafkaConfig) {
        this.producer = producer;
        this.kafkaConfig = kafkaConfig;
        this.topic = KafkaTopic.SENSORS.getTopicName();
    }

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        String topic = kafkaConfig.getTopic(KafkaTopic.SENSORS.getTopicName());
        T payload = mapToAvro(event);
        SensorEventAvro eventAvro = buildEventAvro(event, payload);
        producer.send(eventAvro, eventAvro.getHubId(), eventAvro.getTimestamp(), topic);
        log.info("Событие датчика с типом %s отправлено в топик ".formatted(event.getPayloadCase()), topic);
    }

    private SensorEventAvro buildEventAvro(SensorEventProto event, T payload) {

        final Instant timestamp = Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos())
                .truncatedTo(java.time.temporal.ChronoUnit.MILLIS);

        return SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }
}