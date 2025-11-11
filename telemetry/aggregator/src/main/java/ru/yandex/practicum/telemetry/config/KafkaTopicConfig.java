package ru.yandex.practicum.telemetry.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("kafka.topics")
@Configuration
@Slf4j
public class KafkaTopicConfig {

    private List<String> consumerSubscriptions;
    private String producerTopic;

    @Bean
    public String producerTopic() {
        return producerTopic;
    }

    @Bean
    public List<String> consumerTopic() {
        return consumerSubscriptions;
    }
}
