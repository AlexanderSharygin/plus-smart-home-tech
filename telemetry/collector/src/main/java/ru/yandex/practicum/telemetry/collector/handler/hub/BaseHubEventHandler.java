package ru.yandex.practicum.telemetry.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.config.KafkaTopic;
import ru.yandex.practicum.telemetry.collector.model.models.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;


@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;
    private final KafkaConfig kafkaConfig;

    @Autowired
    protected BaseHubEventHandler(KafkaEventProducer producer, KafkaConfig kafkaConfig) {
        this.producer = producer;
        this.kafkaConfig = kafkaConfig;
    }

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        String topic = kafkaConfig.getTopic(KafkaTopic.HUBS.getTopicName());
        T payload = mapToAvro(event);
        HubEventAvro eventAvro = buildEventAvro(event, payload);
        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), topic);
        log.info("Событие хаба с типом %s отправлено в топик ".formatted(event.getType()), topic);
    }

    private HubEventAvro buildEventAvro(HubEvent event, T payload) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}