package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.repository.SnapshotRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {


    private final SnapshotRepository snapshots;


    @Override
    public Optional<SensorsSnapshotAvro> updateState(final SensorEventAvro event) {


        final SensorsSnapshotAvro snapshot = snapshots.findByHubId(event.getHubId())
                .orElseGet(() -> buildSnapshot(event));

        if (isCurrentSnapshotValid(snapshot, event)) {

            return Optional.empty();
        }

        updateSnapshotData(snapshot, event);

        snapshots.save(snapshot);
        return Optional.of(snapshot);
    }

    private void updateSnapshotData(final SensorsSnapshotAvro snapshot, final SensorEventAvro event) {

        final SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
    }

    private boolean isCurrentSnapshotValid(final SensorsSnapshotAvro currentSnapshot,
                                           final SensorEventAvro event) {

        final SensorStateAvro currentState = currentSnapshot.getSensorsState().get(event.getId());

        return currentState != null &&
                (!currentState.getTimestamp().isBefore(event.getTimestamp()) ||
                        currentState.getData().equals(event.getPayload()));
    }

    private SensorsSnapshotAvro buildSnapshot(final SensorEventAvro event) {

        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(new HashMap<>())
                .build();
    }
}