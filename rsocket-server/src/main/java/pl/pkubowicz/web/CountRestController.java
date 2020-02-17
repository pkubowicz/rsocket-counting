package pl.pkubowicz.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pkubowicz.model.CountService;
import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class CountRestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CountService countService;

    public CountRestController(CountService countService) {
        this.countService = countService;
    }

    @GetMapping("/all")
    public Flux<CountStep> getAll(
            @RequestParam(defaultValue = "0") int delay,
            @RequestParam(defaultValue = "0") int max) {
        logger.info("Serving via HTTP with delay {}", delay);
        return countService.getCount(Duration.ofMillis(delay), max);
    }

    @GetMapping(path = "/all-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CountStep> getAllStream(
            @RequestParam(defaultValue = "0") int delay,
            @RequestParam(defaultValue = "0") int max) {
        logger.info("Serving via HTTP stream with delay {}", delay);
        return countService.getCount(Duration.ofMillis(delay), max);
    }
}
