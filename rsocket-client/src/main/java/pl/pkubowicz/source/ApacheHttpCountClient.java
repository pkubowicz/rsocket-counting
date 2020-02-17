package pl.pkubowicz.source;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class ApacheHttpCountClient implements CountSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String baseUrl;
    private final int max;
    private final ObjectMapper objectMapper;
    private final TypeReference<List<CountStep>> resultType = new TypeReference<>() {
    };

    public ApacheHttpCountClient(
            @Value("${count.server.host}") String host,
            @Value("${count.server.http.port}") int port,
            @Value("${count.max}") int max,
            ObjectMapper objectMapper) {
        this.max = max;
        this.objectMapper = objectMapper;
        this.baseUrl = "http://" + host + ":" + port;
    }

    @Override
    public Flux<CountStep> getAll(Duration serverDelay) {
        logger.info("Requesting {} elements from /all with Apache HttpClient", max);
        return Flux.fromIterable(fetchAll(serverDelay));
    }

    private List<CountStep> fetchAll(Duration serverDelay) {
        try (var httpClient = HttpClients.createDefault()) {
            try (var response = httpClient.execute(
                    new HttpGet(baseUrl + "/all?delay=" + serverDelay.toMillis() + "&max=" + max)
            )) {
                return objectMapper.readValue(response.getEntity().getContent(), resultType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
