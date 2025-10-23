package ru.yandex.practicum.telemetry.collector.model.models.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.ScenarioConditionOperation;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.ScenarioConditionType;


@Setter
@Getter
@ToString
public class ScenarioCondition {

  @NotBlank
  private String sensorId;

  @NotNull
  private ScenarioConditionType type;

  @NotNull
  private ScenarioConditionOperation operation;

  private Integer value;

}
