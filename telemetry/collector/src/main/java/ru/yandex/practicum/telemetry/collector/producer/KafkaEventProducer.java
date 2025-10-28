package ru.yandex.practicum.telemetry.collector.producer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;


@RequiredArgsConstructor
@Component
public class KafkaEventProducer {

    private final Producer<String, SpecificRecordBase> producer;

    public void send(SpecificRecordBase event, String key, Instant timestamp, String topic) {
        try {
            final ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(topic, null, timestamp.toEpochMilli(), key, event);
            producer.send(record);
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось отправить сообщение в топик " + topic, ex);
        }
    }
}
