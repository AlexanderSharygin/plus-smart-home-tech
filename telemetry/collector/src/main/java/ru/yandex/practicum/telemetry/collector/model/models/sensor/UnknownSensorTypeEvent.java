package ru.yandex.practicum.telemetry.collector.model.models.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.enums.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.models.hub.HubEvent;

@Getter
@Setter
@ToString
public class UnknownSensorTypeEvent extends SensorEvent {

    @Override
    public SensorEventType getType() {
        return SensorEventType.UNKNOWN;
    }
}
