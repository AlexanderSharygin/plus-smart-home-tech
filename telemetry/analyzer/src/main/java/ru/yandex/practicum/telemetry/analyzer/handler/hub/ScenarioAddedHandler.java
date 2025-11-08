package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Override
    public String getMessageType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        try {
            ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();
            Scenario scenario = scenarioRepository
                    .findScenarioByHubIdAndNameContainingIgnoreCase(event.getHubId(), scenarioAddedEvent.getName())
                    .orElseGet(() -> scenarioRepository.save(createScenario(event)));

            if (checkScenarioConditionsForSensor(scenarioAddedEvent, event.getHubId())) {
                Set<Condition> conditions = scenarioAddedEvent.getConditions().stream()
                        .map(c -> Condition.builder()
                                .sensor(sensorRepository.findById(c.getSensorId()).orElseThrow())
                                .scenario(scenario)
                                .type(c.getType())
                                .operation(c.getOperation())
                                .value(getFormatedValue(c.getValue()))
                                .build())
                        .collect(Collectors.toSet());
                ;
                conditionRepository.saveAll(conditions);
            }
            if (checkScenarioActionsForSensor(scenarioAddedEvent, event.getHubId())) {
                Set<Action> actions = scenarioAddedEvent.getActions().stream()
                        .map(action -> Action.builder()
                                .sensor(sensorRepository.findById(action.getSensorId()).orElseThrow())
                                .scenario(scenario)
                                .type(action.getType())
                                .value(action.getValue())
                                .build())
                        .collect(Collectors.toSet());
                actionRepository.saveAll(actions);
            }

        } catch (Exception e) {
            log.error("Ошибка сохранения сценария", e);
            throw new RuntimeException("Не удалось сохранить сценарий", e);
        }
    }

    private Scenario createScenario(HubEventAvro hubEvent) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) hubEvent.getPayload();
        return Scenario.builder()
                .name(scenarioAddedEvent.getName())
                .hubId(hubEvent.getHubId())
                .build();
    }

    private Boolean checkScenarioConditionsForSensor(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsSensorsByIdInAndHubId(scenarioAddedEvent.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList(), hubId);
    }

    private Boolean checkScenarioActionsForSensor(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsSensorsByIdInAndHubId(scenarioAddedEvent.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList(), hubId);
    }

    private Integer getFormatedValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }
}