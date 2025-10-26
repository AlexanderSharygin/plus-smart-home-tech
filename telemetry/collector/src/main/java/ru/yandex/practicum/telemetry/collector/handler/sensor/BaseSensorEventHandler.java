package ru.yandex.practicum.telemetry.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.config.KafkaTopic;
import ru.yandex.practicum.telemetry.collector.model.models.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;


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

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        String topic = kafkaConfig.getTopic(KafkaTopic.SENSORS.getTopicName());
        T payload = mapToAvro(event);
        SensorEventAvro eventAvro = buildEventAvro(event, payload);
        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), topic);
        log.info("Событие датчика с типом %s отправлено в топик ".formatted(event.getType()), topic);
    }

    private SensorEventAvro buildEventAvro(SensorEvent event, T payload) {
        return SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}