package pl.pkubowicz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pkubowicz.source.*;
import pl.pkubowicz.subscriber.*;

import java.time.Duration;
import java.util.Map;

@SpringBootApplication
public class App implements CommandLineRunner {

    private final Map<String, CountSource> sources;
    private final CountSource defaultSource;
    private final Duration DEFAULT_DELAY = Duration.ZERO;
    private final Map<String, CountSubscriber.Factory> subscribers;
    private final CountSubscriber.Factory defaultSubscriber;
    private final int iterations;

    public App(
            HttpCountClient httpCountClient,
            ApacheHttpCountClient apacheHttpCountClient,
            EventStreamCountClient eventStreamCountClient,
            RSocketCountClient rSocketCountClient,
            @Value("${count.iterations}") int iterations) {
        sources = Map.of(
                "http", httpCountClient,
                "apache", apacheHttpCountClient,
                "event", eventStreamCountClient,
                "rsocket", rSocketCountClient,
                "local", new LocalCountSource()
        );
        defaultSource = rSocketCountClient;
        subscribers = Map.of(
                "aggregating", AggregatingCountSubscriber::new,
                "finding", FindingCountSubscriber::new,
                "printing", PrintingCountSubscriber::new,
                "reducing", ReducingCountSubscriber::new
        );
        defaultSubscriber = PrintingCountSubscriber::new;
        this.iterations = iterations;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        CountSource countSource = getSource(args, 0);
        Duration delay = getDelay(args, 1);
        Duration serverDelay = getDelay(args, 2);
        var subscriberFactory = getSubscriber(args, 3);
        new CountRunner(countSource, delay, serverDelay, subscriberFactory).run(iterations);
    }

    private CountSource getSource(String[] args, int argIndex) {
        return findValue(args, argIndex, defaultSource, sources);
    }

    private Duration getDelay(String[] args, int argIndex) {
        if (args.length <= argIndex) {
            return DEFAULT_DELAY;
        }
        var delay = Duration.ofMillis(Integer.parseInt(args[argIndex]));
        if (delay.isNegative()) {
            throw new IllegalArgumentException("cannot specify a negative delay");
        }
        return delay;
    }

    private CountSubscriber.Factory getSubscriber(String[] args, int argIndex) {
        return findValue(args, argIndex, defaultSubscriber, subscribers);
    }

    private static <T> T findValue(String[] args, int argIndex, T defaultValue, Map<String, T> nameToValue) {
        if (args.length <= argIndex) {
            return defaultValue;
        }
        String sourceName = args[argIndex].toLowerCase();
        return nameToValue.entrySet().stream()
                .filter(e -> e.getKey().startsWith(sourceName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(defaultValue);
    }
}
