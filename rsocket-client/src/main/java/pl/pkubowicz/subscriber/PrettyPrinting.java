package pl.pkubowicz.subscriber;

import org.slf4j.Logger;

final class PrettyPrinting {
    private PrettyPrinting() {
    }

    static void printOrDot(Logger logger, Runnable traceLogging) {
        if (logger.isTraceEnabled()) {
            traceLogging.run();
        } else if (logger.isDebugEnabled()) {
            // by default we print just dots in a single line then finish with a single newline
            // we cannot use logger because it will put each dot in a new line
            // logically this is a debug-level log as we don't want to print dots during performance tests
            System.out.print(".");
        }
    }

    static void finishDotsLine(Logger logger) {
        if (!logger.isTraceEnabled() && logger.isDebugEnabled()) {
            System.out.println();
        }
    }
}
