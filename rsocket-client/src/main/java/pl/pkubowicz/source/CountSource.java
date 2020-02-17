package pl.pkubowicz.source;

import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface CountSource {
    Flux<CountStep> getAll(Duration serverDelay);
}
