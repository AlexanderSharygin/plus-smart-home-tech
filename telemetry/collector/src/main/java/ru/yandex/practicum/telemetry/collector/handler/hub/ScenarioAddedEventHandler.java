package ru.yandex.practicum.telemetry.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaEventProducer producer, KafkaConfig kafkaConfig) {
        super(producer, kafkaConfig);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setActions(mapListToAvro(scenarioAddedEvent.getActionList(), this::mapToAvro))
                .setConditions(mapListToAvro(scenarioAddedEvent.getConditionList(), this::mapToAvro))
                .build();
    }

    private DeviceActionAvro mapToAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .build();
    }

    private ScenarioConditionAvro mapToAvro(ScenarioConditionProto condition) {
        ScenarioConditionAvro scenarioConditionAvro = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .build();

        switch (condition.getValueCase()) {
            case BOOL_VALUE:
                scenarioConditionAvro.setValue(condition.getBoolValue());
                break;
            case INT_VALUE:
                scenarioConditionAvro.setValue(condition.getIntValue());
                break;
            default:
                scenarioConditionAvro.setValue(null);
        }
        return scenarioConditionAvro;
    }

    private <T, R> List<R> mapListToAvro(List<T> source, Function<T, R> mapper) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        return source.stream()
                .map(mapper)
                .toList();
    }
}