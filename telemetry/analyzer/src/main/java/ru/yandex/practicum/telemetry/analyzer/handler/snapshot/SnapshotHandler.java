package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.grpc.GrpcClient;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final GrpcClient grpcClient;

    public void buildSnapshot(SensorsSnapshotAvro sensorsSnapshot) {
        try {
            Map<String, SensorStateAvro> sensorStateMap = sensorsSnapshot.getSensorsState();
            List<Scenario> scenarios = scenarioRepository.findScenariosByHubId(sensorsSnapshot.getHubId());
            scenarios.forEach(scenario -> {
                if (isScenarioConditionsMet(scenario, sensorStateMap)) {
                    sendScenarioActions(scenario);
                }
            });
        } catch (RuntimeException e) {
            log.error("Ошибка во время обработки snapshot", e);
        }
    }

    private boolean isScenarioConditionsMet(Scenario scenario, Map<String, SensorStateAvro> sensorsStates) {
        List<Condition> conditions = conditionRepository.findConditionsByScenario(scenario);
        return conditions.stream().allMatch(condition -> checkCondition(condition, sensorsStates));
    }

    private boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorsStates) {
        String sensorId = condition.getSensor().getId();
        SensorStateAvro sensorState = sensorsStates.get(sensorId);
        if (sensorState == null) {
            return false;
        }

        switch (condition.getType()) {
            case LUMINOSITY -> {
                LightSensorAvro lightSensor = (LightSensorAvro) sensorState.getData();
                return handleOperation(condition, lightSensor.getLuminosity());
            }
            case TEMPERATURE -> {
                ClimateSensorAvro temperatureSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, temperatureSensor.getTemperatureC());
            }
            case MOTION -> {
                MotionSensorAvro motionSensor = (MotionSensorAvro) sensorState.getData();
                return handleOperation(condition, motionSensor.getMotion() ? 1 : 0);
            }
            case SWITCH -> {
                SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorState.getData();
                return handleOperation(condition, switchSensor.getState() ? 1 : 0);
            }
            case CO2LEVEL -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getCo2Level());
            }
            case HUMIDITY -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getHumidity());
            }
            case null -> {
                return false;
            }
        }
    }

    private boolean handleOperation(Condition condition, Integer currentValue) {
        ConditionOperationAvro conditionOperation = condition.getOperation();
        Integer targetValue = condition.getValue();

        switch (conditionOperation) {
            case EQUALS -> {
                return targetValue.equals(currentValue);
            }
            case LOWER_THAN -> {
                return currentValue < targetValue;
            }
            case GREATER_THAN -> {
                return currentValue > targetValue;
            }
            default -> {
                return false;
            }
        }
    }

    private void sendScenarioActions(Scenario scenario) {
        try {
            List<Action> actions = actionRepository.findActionsByScenario(scenario);
            actions.forEach(action -> {
                try {
                    grpcClient.sendRequest(action);
                } catch (Exception e) {
                    log.error("Ошибка выполнения действия действия:  {}", action.toString(), e);
                }
            });
        } catch (Exception e) {
            log.error("Ошибка обработки сценария {}", scenario.toString(), e);
        }
    }
}