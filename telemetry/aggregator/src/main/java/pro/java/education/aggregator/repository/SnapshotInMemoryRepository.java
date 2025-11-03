package pro.java.education.aggregator.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class SnapshotInMemoryRepository implements SnapshotRepository {

  private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

  @Override
  public void save(final SensorsSnapshotAvro snapshot) {
    snapshots.put(snapshot.getHubId(), snapshot);
  }

  @Override
  public Optional<SensorsSnapshotAvro> findByHubId(final String hubId) {
    return Optional.ofNullable(snapshots.get(hubId));
  }
}
