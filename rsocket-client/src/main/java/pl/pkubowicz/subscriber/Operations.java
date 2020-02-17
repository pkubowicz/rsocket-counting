package pl.pkubowicz.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static pl.pkubowicz.subscriber.PrettyPrinting.printOrDot;

class Operations {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Duration delay;

    Operations(Duration delay) {
        this.delay = delay;
    }

    int expensiveSum(int i1, int i2) {
        if (!delay.isZero()) {
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        printOrDot(logger, () -> logger.trace("Summing {} and {}", i1, i2));

        return i1 + i2;
    }
}
