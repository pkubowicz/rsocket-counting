package pl.pkubowicz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pkubowicz.source.CountSource;
import pl.pkubowicz.subscriber.Callbacks;
import pl.pkubowicz.subscriber.CountSubscriber;

import java.time.Duration;
import java.util.stream.IntStream;

public class CountRunner {
    private static final int WARMUP_ITERATIONS = 10;
    private static final Logger logger = LoggerFactory.getLogger(CountRunner.class);
    private final CountSource countSource;
    private final Duration delay;
    private final Duration serverDelay;
    private final CountSubscriber.Factory subscriberFactory;
    private boolean warmup;

    public CountRunner(CountSource countSource, Duration delay, Duration serverDelay, CountSubscriber.Factory subscriberFactory) {
        this.countSource = countSource;
        this.delay = delay;
        this.serverDelay = serverDelay;
        this.subscriberFactory = subscriberFactory;
    }

    public void run(int iterations) {
        IntStream.range(0, iterations).forEach(iteration -> {
            warmup = isWarmup(iteration, iterations);
            runIteration();
        });
    }

    private void runIteration() {
        Thread mainThread = Thread.currentThread();
        var startTime = System.nanoTime();
        Runnable finishNormally = () -> {
            printTimeElapsed("Finished", startTime);
            mainThread.interrupt();
        };
        var callbacks = new Callbacks(finishNormally, Throwable::printStackTrace);
        var processor = new CountProcessor(() -> printTimeElapsed("First element", startTime));

        subscriberFactory.create(delay, callbacks).consume(
                processor.process(countSource.getAll(serverDelay))
        );

        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            // expected :)
        }
    }

    private void printTimeElapsed(String eventType, long startTime) {
        var endTime = System.nanoTime();
        logger.info(String.format(
                "%s%s in %,d ms",
                warmup ? "(warmup) " : "", eventType, Math.round((endTime - startTime) / (double) 1000_000)
        ));
    }

    private static boolean isWarmup(int iteration, int iterations) {
        return iterations > WARMUP_ITERATIONS && iteration < WARMUP_ITERATIONS;
    }
}
