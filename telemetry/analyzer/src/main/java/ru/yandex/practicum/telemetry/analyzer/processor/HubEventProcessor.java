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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable  {

    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final List<String> topics;
    private  final Map<String, HubEventHandler> eventHandlers;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer,
                            KafkaTopicConfig topicsConfig,
                             KafkaConsumerHubsConfig consumerConfig,
                              Set<HubEventHandler> eventHandlers) {
        this.consumer = consumer;
        this.topics = topicsConfig.getHubs();

        this.eventHandlers = eventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            log.info("Запуск обработчика событий для топиков: {}", topics.toString());
            consumer.subscribe(topics);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал завершения работы.");
                consumer.wakeup();
            }));
            log.debug("Доступно обработчиков событий: {}", eventHandlers.size());

            while (true) {
                log.trace("Ожидание новых событий...");
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(1000);

                if (!records.isEmpty()) {
                    log.info("Получено {} событий для обработки", records.count());
                }

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String eventType = event.getPayload().getClass().getSimpleName();
                    String hubId = event.getHubId();
                    log.info("Обработка события {} для хаба {}, смещение: {}",
                            eventType, hubId, record.offset());
                    HubEventHandler handler = eventHandlers.get(eventType);
                    if (handler != null) {
                        log.debug("Найден обработчик для типа события {}", eventType);
                        handler.handle(event);
                        log.info("Событие {} для хаба {} успешно обработано",
                                eventType, hubId);
                    } else {
                        throw new IllegalArgumentException("Не найден обработчик для типа события " + eventType);
                    }
                }
                log.debug("Фиксация смещений для обработанных событий");
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в цикле обработки событий по топикам {}", topics.toString(), e);
        } finally {
            try {
                consumer.commitSync();
                log.info("Смещения зафиксированы перед закрытие консьюмера");
            } finally {
                consumer.close();
                log.info("Консьюмер закрыт");
            }
        }
    }


}
