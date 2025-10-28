package ru.yandex.practicum.telemetry.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.models.hub.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.models.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

@Service
@Slf4j
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaEventProducer producer, KafkaConfig kafkaConfig) {
        super(producer, kafkaConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent addedEvent = (DeviceAddedEvent) event;
        DeviceTypeAvro deviceType = DeviceTypeAvro.valueOf(addedEvent.getDeviceType().name());
        return DeviceAddedEventAvro.newBuilder()
                .setId(addedEvent.getId())
                .setType(deviceType)
                .build();
    }
}