package pl.pkubowicz.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.time.Duration;

class DelayedProducer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int max;
    private final Duration delay;
    private Thread thread;

    public DelayedProducer(int max, Duration delay) {
        this.max = max;
        this.delay = delay;
    }

    void start(FluxSink<Integer> sink) {
        this.thread = new Thread(() -> {
            for (int number = 0; number < max; number++) {
                logger.debug("Producing {}", number);
                sink.next(number);
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException e) {
                    logger.info("Interrupted at {}", number);
                    break;
                }
            }
            sink.complete();
        });
        thread.start();
    }

    void stop() {
        logger.info("Interrupting");
        thread.interrupt();
    }
}
