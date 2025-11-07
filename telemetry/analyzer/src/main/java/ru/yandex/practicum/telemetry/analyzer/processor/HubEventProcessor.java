package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaConsumerHubsConfig;
import ru.yandex.practicum.telemetry.analyzer.config.KafkaTopicConfig;
import ru.yandex.practicum.telemetry.analyzer.handler.hub.HubEventHandler;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final KafkaConsumerHubsConfig consumerConfig;
    private final KafkaTopicConfig topicsConfig;
    private final Map<String, HubEventHandler> eventHandlers;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer,
                             KafkaTopicConfig topicsConfig,
                             KafkaConsumerHubsConfig consumerConfig,
                             Set<HubEventHandler> eventHandlers) {
        this.consumer = consumer;
        this.topicsConfig = topicsConfig;
        this.consumerConfig = consumerConfig;
        this.eventHandlers = eventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(topicsConfig.getHubs());
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer
                        .poll(Duration.ofMillis(consumerConfig.getConsumeAttemptTimeoutMs()));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String eventType = event.getPayload().getClass().getSimpleName();
                    HubEventHandler handler = eventHandlers.get(eventType);
                    if (handler != null) {
                        handler.handle(event);
                    } else {
                        log.error("Неизвестное событие с типом {}", eventType);
                        throw new IllegalArgumentException("Неизвестное событие с типом " + eventType);
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка при обработки событий hubs", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}