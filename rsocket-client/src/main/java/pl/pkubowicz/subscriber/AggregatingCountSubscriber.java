package pl.pkubowicz.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static pl.pkubowicz.subscriber.PrettyPrinting.finishDotsLine;

public class AggregatingCountSubscriber implements CountSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Duration delay;
    private final Callbacks callbacks;

    public AggregatingCountSubscriber(Duration delay, Callbacks callbacks) {
        this.delay = delay;
        this.callbacks = callbacks;
    }

    @Override
    public void consume(Flux<Integer> counts) {
        logger.info("Aggregating with delay {}", delay.toMillis());
        var operations = new Operations(delay);
        counts
                .collectList()
                .doOnError(callbacks.errorHandler)
                .doOnCancel(callbacks.finishHandler)
                .subscribe(list -> {
                    var aggregated = list.stream().reduce(0, operations::expensiveSum);
                    finishDotsLine(logger);
                    logger.info("Sum is {}", aggregated);
                    callbacks.finishHandler.run();
                });
    }
}
