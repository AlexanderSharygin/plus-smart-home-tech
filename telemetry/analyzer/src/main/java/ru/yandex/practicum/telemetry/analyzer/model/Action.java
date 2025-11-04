package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an action to perform as part of a scenario.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "actions")
@EqualsAndHashCode(of = {"id"})
@Builder
public class Action {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ActionTypeAvro type;

  @Column(name = "value")
  private Integer value;

  @ManyToOne
  @JoinColumn(name = "scenario_id", table = "scenario_actions")
  private Scenario scenario;

  @ManyToOne
  @JoinColumn(name = "sensor_id", table = "scenario_actions")
  private Sensor sensor;

}
