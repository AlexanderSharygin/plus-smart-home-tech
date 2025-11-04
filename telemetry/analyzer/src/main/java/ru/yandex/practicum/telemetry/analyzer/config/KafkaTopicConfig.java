package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.topics")
@Configuration
@Slf4j
public class KafkaTopicConfig {

  private String snapshots;
  private String hubs;

  @Bean
  public String producerTopic() {
    return hubs;
  }

  @Bean
  public String consumerTopic() {
    return snapshots;
  }
}
