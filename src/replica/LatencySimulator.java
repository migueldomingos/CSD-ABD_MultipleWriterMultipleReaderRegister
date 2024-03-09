package replica;

import lombok.SneakyThrows;

/**
 * Simulates latency in the network following an exponential distribution
 * The average wait time (ms) for a response is SLEEP_INTERVAL / SUCCESS_PROBABILITY
 * The minimum wait time for a response is SLEEP_INTERVAL and there is no maximum wait time (asynchronous network)
 */
public class LatencySimulator {

    private static final int SLEEP_INTERVAL = 100;

    private static final float SUCCESS_PROBABILITY = 0.02f;

    @SneakyThrows
    public static void simulateLatency() {
        do {
            Thread.sleep(SLEEP_INTERVAL);
        } while (Math.random() > SUCCESS_PROBABILITY);
    }

}
