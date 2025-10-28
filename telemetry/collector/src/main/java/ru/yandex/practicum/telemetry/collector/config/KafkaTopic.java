package ru.yandex.practicum.telemetry.collector.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum KafkaTopic {
    SENSORS("sensors"),
    HUBS("hubs");

    private final String topicName;
}
