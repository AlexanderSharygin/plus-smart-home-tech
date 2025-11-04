package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a condition that must be met for a scenario to be triggered.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conditions")
@EqualsAndHashCode(of = "id")
@Builder
public class Condition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ConditionTypeAvro type;

  @Column(name = "operation", nullable = false)
  @Enumerated(EnumType.STRING)
  private ConditionOperationAvro operation;

  @Column(name = "value", nullable = false)
  private Integer value;

  @ManyToOne
  @JoinColumn(name = "scenario_id", table = "scenario_conditions")
  private Scenario scenario;

  @ManyToOne
  @JoinColumn(name = "sensor_id", table = "scenario_conditions")
  private Sensor sensor;
}