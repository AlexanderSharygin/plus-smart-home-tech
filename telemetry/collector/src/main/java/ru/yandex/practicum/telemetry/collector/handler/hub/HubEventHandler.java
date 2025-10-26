package ru.yandex.practicum.telemetry.collector.handler.hub;

import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.models.hub.HubEvent;


public interface HubEventHandler {

    HubEventType getMessageType();

    void handle(HubEvent event);

}
