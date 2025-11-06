package ru.yandex.practicum.kafka.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorsSnapshotDeserializer extends BaseAvroDeserializer<SensorEventAvro> {
    public SensorsSnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
