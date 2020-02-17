package pl.pkubowicz.subscriber;

import reactor.core.publisher.Flux;

import java.time.Duration;

public interface CountSubscriber {
    void consume(Flux<Integer> counts);

    @FunctionalInterface
    interface Factory {
        CountSubscriber create(Duration delay, Callbacks callbacks);
    }
}
