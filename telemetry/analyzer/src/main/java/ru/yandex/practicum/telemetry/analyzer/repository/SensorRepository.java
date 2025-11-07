package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;

import java.util.Collection;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    void deleteSensorByIdAndHubId(String id, String hubId);

    boolean existsSensorsByIdInAndHubId(Collection<String> ids, String hubId);
}