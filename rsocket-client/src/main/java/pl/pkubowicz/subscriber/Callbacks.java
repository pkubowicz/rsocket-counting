package pl.pkubowicz.subscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class Callbacks {
    final Runnable finishHandler;
    final Consumer<? super Throwable> errorHandler;

    public Callbacks(Runnable finishHandler, Consumer<? super Throwable> errorHandler) {
        this.finishHandler = finishHandler;
        this.errorHandler = errorHandler;
    }

    <T> Flux<T> applyTo(Flux<T> flux) {
        return flux
                .doOnError(errorHandler)
                .doOnTerminate(finishHandler)
                .doOnCancel(finishHandler);
    }

    <T> Mono<T> applyTo(Mono<T> mono) {
        return mono
                .doOnError(errorHandler)
                .doOnTerminate(finishHandler)
                .doOnCancel(finishHandler);
    }
}
