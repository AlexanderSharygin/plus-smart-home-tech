package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "scenarios")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id")
    private String hubId;


    private String name;

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", hubId='" + hubId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}