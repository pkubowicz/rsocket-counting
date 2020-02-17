package pl.pkubowicz.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class CountService {
    private static final int DEFAULT_MAX = 50;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Flux<CountStep> getCount(Duration delay, int max) {
        int effectiveMax = max > 0 ? max : DEFAULT_MAX;
        return (delay.isZero() ? generateImmediate(effectiveMax) : generateDelayed(delay, effectiveMax))
                .map(CountStep::new)
                .doOnRequest(requested -> logger.debug("Network request={}", requested));
    }

    public Flux<Integer> generateImmediate(int max) {
        return Flux
                .generate(() -> 0, (state, sink) -> {
                    logger.debug("Generating {}", state);
                    sink.next(state);
                    return state + 1;
                })
                .doOnRequest(requested -> logger.debug("Received request internal={}", requested))
                .cast(Integer.class)
                .take(max);
    }

    public Flux<Integer> generateDelayed(Duration delay, int max) {
        DelayedProducer delayedProducer = new DelayedProducer(max, delay);
        return Flux.create(delayedProducer::start)
                .doOnCancel(delayedProducer::stop);
    }
}
