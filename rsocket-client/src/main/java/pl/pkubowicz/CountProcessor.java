package pl.pkubowicz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;

public class CountProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Runnable onFirstResultReceived;

    public CountProcessor(Runnable onFirstResultReceived) {
        this.onFirstResultReceived = onFirstResultReceived;
    }

    public Flux<Integer> process(Flux<CountStep> counts) {
        var firstAppeared = new AtomicBoolean();
        var result = counts
                .doOnRequest(requested -> logger.debug("Requested {} elements ", requested))
                .doOnNext(ignored -> {
                    if (firstAppeared.compareAndSet(false, true)) {
                        onFirstResultReceived.run();
                    }
                })
                .map(CountStep::getValue);
//        result = result.take(10);
//        result = result.limitRate(7);
        return result;
    }
}
