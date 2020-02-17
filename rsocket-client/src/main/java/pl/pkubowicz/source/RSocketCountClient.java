package pl.pkubowicz.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RSocketCountClient implements CountSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Mono<RSocketRequester> requesterMono;
    private final int max;

    public RSocketCountClient(
            RSocketRequester.Builder rsocketRequesterBuilder,
            @Value("${count.server.host}") String host,
            @Value("${count.server.rsocket.port}") int port,
            @Value("${count.max}") int max
    ) {
        this.max = max;
        this.requesterMono = rsocketRequesterBuilder.connectTcp(host, port);
    }

    @Override
    public Flux<CountStep> getAll(Duration serverDelay) {
        return requesterMono.flatMapMany(requester -> {
                    logger.info("Requesting {} elements from count.all via RSocket", max);
                    return requester.route(
                            "count.all." + serverDelay.toMillis() + "." + max
                    ).retrieveFlux(CountStep.class);
                }
        );
    }
}
