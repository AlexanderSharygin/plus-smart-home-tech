package ru.yandex.practicum.telemetry.collector.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.models.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.models.sensor.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

@Service
@Slf4j
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(KafkaEventProducer producer, KafkaConfig kafkaConfig) {
        super(producer, kafkaConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        SwitchSensorEvent _event = (SwitchSensorEvent) event;
        return SwitchSensorAvro.newBuilder()
                .setState(_event.getState())
                .build();
    }
}