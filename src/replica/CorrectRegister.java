package replica;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
public class CorrectRegister implements Register {

    private static final Logger logger = LogManager.getLogger(CorrectRegister.class);

    private float value;

    private int timestamp;

    public CorrectRegister() {
        value = -1;
        timestamp = -1;
    }

    @Override
    public void updateRegister(int timestamp, float value) {
        if (timestamp > this.timestamp) {
            if (timestamp - this.timestamp > 1)
                logger.warn("Timestamp difference is {}, possible message loss or out of order delivery.",
                        timestamp - this.timestamp);
            logger.trace("Updating register with timestamp: {} and value: {}", timestamp, value);
            this.timestamp = timestamp;
            this.value = value;
        } else {
            logger.warn("Received outdated timestamp: {}, current timestamp: {}", timestamp, this.timestamp);
        }
    }
}
