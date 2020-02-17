package pl.pkubowicz.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class FindingCountSubscriber implements CountSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Duration delay;
    private final Callbacks callbacks;

    public FindingCountSubscriber(Duration delay, Callbacks callbacks) {
        this.delay = delay;
        this.callbacks = callbacks;
    }

    @Override
    public void consume(Flux<Integer> counts) {
        counts
                .filterWhen(number -> {
                    logger.debug("Checking {}", number);
                    if (!delay.isZero()) {
                        try {
                            Thread.sleep(delay.toMillis());
                        } catch (InterruptedException e) {
                            return Mono.error(e);
                        }
                    }
                    return Mono.just(number == 9);
                })
                .transform(callbacks::applyTo)
                .next()
                .subscribe(number -> logger.info("Found {}", number));
    }

}
