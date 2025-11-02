package pro.java.education.aggregator.config;

import jakarta.annotation.PostConstruct;
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

  private List<String> consumerSubscription;
  private String producerTopic;

  @Bean
  public String producerTopic() {
    return producerTopic;
  }

  @Bean
  public List<String> consumerTopic() {
    return consumerSubscription;
  }
}
