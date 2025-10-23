package ru.yandex.practicum.telemetry.collector.model.models.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.DeviceActionType;

@Setter
@Getter
@ToString
public class DeviceAction {

  @NotBlank
  private String sensorId;

  @NotNull
  private DeviceActionType type;

  private Integer value;
}
