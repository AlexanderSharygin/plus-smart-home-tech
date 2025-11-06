package ru.yandex.practicum.telemetry.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.processor.HubEventProcessor;
import ru.yandex.practicum.telemetry.analyzer.processor.SnapshotProcessor;


@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        try {
            Thread hubEventsThread = new Thread(hubEventProcessor);
            hubEventsThread.setName("HubEventHandlerThread");
            hubEventsThread.start();
            snapshotProcessor.run();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось запустить обработчики", e);
        }
    }
}
