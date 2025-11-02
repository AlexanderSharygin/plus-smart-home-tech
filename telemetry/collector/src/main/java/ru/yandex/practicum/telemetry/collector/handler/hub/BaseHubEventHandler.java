package ru.yandex.practicum.telemetry.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.config.KafkaTopic;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

import java.time.Instant;


@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;
    private final KafkaConfig kafkaConfig;

    @Autowired
    protected BaseHubEventHandler(KafkaEventProducer producer, KafkaConfig kafkaConfig) {
        this.producer = producer;
        this.kafkaConfig = kafkaConfig;
    }

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        String topic = kafkaConfig.getTopic(KafkaTopic.HUBS.getTopicName());
        T payload = mapToAvro(event);
        HubEventAvro eventAvro = buildEventAvro(event, payload);
        producer.send(eventAvro, eventAvro.getHubId(), eventAvro.getTimestamp(), topic);
        log.info("Событие хаба с типом %s отправлено в топик ".formatted(event.getPayloadCase()), topic);
    }

    private HubEventAvro buildEventAvro(HubEventProto event, T payload) {
        final Instant timestamp = Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos())
                .truncatedTo(java.time.temporal.ChronoUnit.MILLIS);

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }
}