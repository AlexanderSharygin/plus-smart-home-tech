package ru.yandex.practicum.telemetry.collector.model.models.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.sensor.SensorEventType;


@Setter
@Getter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {

  @NotNull
  private Integer temperatureC;

  @NotNull
  private Integer temperatureF;

  @Override
  public SensorEventType getType() {
    return SensorEventType.TEMPERATURE_SENSOR_EVENT;
  }
}
