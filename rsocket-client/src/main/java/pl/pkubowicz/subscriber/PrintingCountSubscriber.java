package pl.pkubowicz.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static pl.pkubowicz.subscriber.PrettyPrinting.finishDotsLine;
import static pl.pkubowicz.subscriber.PrettyPrinting.printOrDot;

public class PrintingCountSubscriber implements CountSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Duration delay;
    private final Callbacks callbacks;

    public PrintingCountSubscriber(Duration delay, Callbacks callbacks) {
        this.delay = delay;
        this.callbacks = callbacks;
    }

    @Override
    public void consume(Flux<Integer> counts) {
        counts
                .doOnTerminate(() -> finishDotsLine(logger))
                .transform(callbacks::applyTo)
                .subscribe(number -> {
                    applyDelay();
                    printOrDot(logger, () -> logger.trace("Consuming {}", number));
                });
    }

    private void applyDelay() {
        if (!delay.isZero()) {
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
