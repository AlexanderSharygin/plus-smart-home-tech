package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

import java.util.List;

public interface ConditionRepository extends JpaRepository<Condition, Long> {

    void deleteConditionsByScenario(Scenario scenario);

    List<Condition> findConditionsByScenario(Scenario scenario);
}
