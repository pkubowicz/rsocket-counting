package pl.pkubowicz.source;

import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;

public class LocalCountSource implements CountSource {
    @Override
    public Flux<CountStep> getAll(Duration serverDelay) {
        return Flux
                .generate(() -> 0, (state, sink) -> {
                    System.out.println("Producing " + state);
                    sink.next(state);
                    return state + 1;
                })
                .cast(Integer.class)
                .map(i -> new CountStep(i, i.toString(), i.toString(), i.toString(), Collections.emptyList()))
                .doOnRequest(requested -> System.out.println("Received request=" + requested))
                .take(20)
//                .publishOn(Schedulers.parallel())
                ;
    }
}
