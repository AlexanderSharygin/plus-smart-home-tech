package pro.java.education.aggregator.repository;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;


public interface SnapshotRepository {

  void save(SensorsSnapshotAvro snapshot);

  Optional<SensorsSnapshotAvro> findByHubId(String hubId);
}
