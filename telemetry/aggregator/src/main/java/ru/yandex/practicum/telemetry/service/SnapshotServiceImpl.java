package ru.yandex.practicum.telemetry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.repository.SnapshotInMemoryRepository;
import ru.yandex.practicum.telemetry.repository.SnapshotRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final SnapshotRepository snapshotRepository;

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {


        SensorsSnapshotAvro snapshot = snapshotRepository.findByHubId(event.getHubId())
                .orElseGet(() -> createSnapshot(event));

        if (isCurrentSnapshotValid(snapshot, event)) {
            return Optional.empty();
        }

        updateSnapshotData(snapshot, event);

        snapshotRepository.save(snapshot);
        return Optional.of(snapshot);
    }

    private void updateSnapshotData(final SensorsSnapshotAvro snapshot, final SensorEventAvro event) {
         SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
    }

    private boolean isSnapshotShouldBeUpdated(SensorsSnapshotAvro currentSnapshot, SensorEventAvro event) {
        SensorStateAvro state = currentSnapshot.getSensorsState().get(event.getId());
        if (state == null) {
            return false;
        }

        if (event.getTimestamp().isAfter(state.getTimestamp()) || event.getTimestamp().equals(state.getTimestamp())) {
            return !state.getData().equals(event.getPayload());
        } else {
            return false;
        }
    }

    private SensorsSnapshotAvro createSnapshot(SensorEventAvro event) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(new HashMap<>())
                .build();
    }

    private boolean isCurrentSnapshotValid(final SensorsSnapshotAvro currentSnapshot,
                                           final SensorEventAvro event) {


        SensorStateAvro currentState = currentSnapshot.getSensorsState().get(event.getId());
        if (currentState == null) {
            return false;
        }
        if (event.getTimestamp().isBefore(currentState.getTimestamp())) {
            return true;
        }
        return event.getPayload().equals(currentState.getData());
    }
}