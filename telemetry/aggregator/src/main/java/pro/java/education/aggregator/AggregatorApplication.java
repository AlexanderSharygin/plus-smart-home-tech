package pro.java.education.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pro.java.education.aggregator.service.AggregationStarter;

@SpringBootApplication
public class AggregatorApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApplication.class, args);
        AggregationStarter aggregator = context.getBean(AggregationStarter.class);
        aggregator.start();
    }
}
