package pl.pkubowicz.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static pl.pkubowicz.subscriber.PrettyPrinting.finishDotsLine;

public class ReducingCountSubscriber implements CountSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Duration delay;
    private final Callbacks callbacks;

    public ReducingCountSubscriber(Duration delay, Callbacks callbacks) {
        this.delay = delay;
        this.callbacks = callbacks;
    }

    @Override
    public void consume(Flux<Integer> counts) {
        logger.info("Reducing with delay {}", delay.toMillis());
        var operations = new Operations(delay);
        counts
                .reduce(0, operations::expensiveSum)
                .doOnError(callbacks.errorHandler)
                .doOnCancel(callbacks.finishHandler)
                .subscribe(sum -> {
                    finishDotsLine(logger);
                    logger.info("Reduced sum is {}", sum);
                    callbacks.finishHandler.run();
                });
    }

}
