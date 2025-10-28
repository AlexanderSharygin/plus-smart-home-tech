package ru.yandex.practicum.telemetry.collector.model.models.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class UnknownHubEvent extends HubEvent {

    @Override
    public HubEventType getType() {
        return HubEventType.UNKNOWN;
    }
}
