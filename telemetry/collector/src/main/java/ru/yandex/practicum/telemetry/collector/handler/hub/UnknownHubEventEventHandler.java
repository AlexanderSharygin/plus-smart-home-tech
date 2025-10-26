package ru.yandex.practicum.telemetry.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.models.hub.HubEvent;


@Slf4j
public class UnknownHubEventEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    @Override
    public HubEventType getMessageType() {
        return HubEventType.UNKNOWN;
    }

    @Override
    public void handle(HubEvent event) {
        log.warn("Не найден обработчик для события хаба с типом {}", event.getType());
        throw new IllegalArgumentException("Не найден обработчик для события хаба с типом " + event.getType());
    }
}