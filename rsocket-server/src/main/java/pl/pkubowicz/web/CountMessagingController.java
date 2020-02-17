package pl.pkubowicz.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import pl.pkubowicz.model.CountService;
import pl.pkubowicz.model.CountStep;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class CountMessagingController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CountService countService;

    public CountMessagingController(CountService countService) {
        this.countService = countService;
    }

    @MessageMapping("count.all.{delay}.{max}")
    public Flux<CountStep> getAll(
            @DestinationVariable int delay,
            @DestinationVariable int max) {
        logger.info("Serving via RSocket with delay {}", delay);
        return countService.getCount(Duration.ofMillis(delay), max);
    }
}
