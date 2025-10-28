package ru.yandex.practicum.telemetry.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.telemetry.collector.handler.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.enums.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.models.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.models.sensor.SensorEvent;


@Slf4j
public class UnknownSensorTypeEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.UNKNOWN;
    }

    @Override
    public void handle(SensorEvent event) {
        log.warn("Не найден обработчик для события датчика с типом {}", event.getType());
        throw new IllegalArgumentException("Не найден обработчик для события датчика с типом " + event.getType());
    }
}