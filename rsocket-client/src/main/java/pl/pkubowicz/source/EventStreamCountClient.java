package pl.pkubowicz.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
public class EventStreamCountClient implements CountSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final WebClient webClient;
    private final ParameterizedTypeReference<ServerSentEvent<CountStep>> resultType = new ParameterizedTypeReference<>() {
    };
    private final int max;

    public EventStreamCountClient(
            WebClient.Builder webClientBuilder,
            @Value("${count.server.host}") String host,
            @Value("${count.server.http.port}") int port,
            @Value("${count.max}") int max
    ) {
        this.max = max;
        this.webClient = webClientBuilder.baseUrl("http://" + host + ":" + port).build();
    }

    @Override
    public Flux<CountStep> getAll(Duration serverDelay) {
        logger.info("Requesting {} elements from /all-stream via SSE", max);
        return webClient.get().uri("/all-stream?delay=" + serverDelay.toMillis() + "&max=" + max)
                .retrieve().bodyToFlux(resultType)
                .map(ServerSentEvent::data);
    }
}
