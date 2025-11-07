package ru.yandex.practicum.telemetry.analyzer.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Action;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcClient {
    @net.devh.boot.grpc.client.inject.GrpcClient("hub-router")
    private HubRouterControllerBlockingStub hubRouterClient;

    public GrpcClient(HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendRequest(Action action) {
        try {
            Timestamp timestamp = Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond())
                    .setNanos(Instant.now().getNano()).build();

            DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                    .setHubId(action.getScenario().getHubId())
                    .setScenarioName(action.getScenario().getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(action.getSensor().getId())
                            .setType(getProtoActionType(action.getType()))
                            .setValue(action.getValue())
                            .build())
                    .setTimestamp(timestamp).build();

            hubRouterClient.handleDeviceAction(deviceActionRequest);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка отправки в HubRouter.  {}", e.getStatus().getDescription(), e);
            throw new RuntimeException("Не удалось отправить действие в HubRouter", e);
        }
    }

    private ActionTypeProto getProtoActionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}
