package replica;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojos.RegisterContentPojo;


public class CorrectRegister implements Register {

    private static final Logger logger = LogManager.getLogger(CorrectRegister.class);

    /**
     * The timestamp/sequence number of the last update.
     */
    private int timestamp;

    /**
     * The id of the replica that last updated the register.
     */
    private int id;

    /**
     * The value of the register.
     */
    private float value;

    public CorrectRegister() {
        value = -1;
        id = -1;
        timestamp = -1;
    }

    @Override
    public RegisterContentPojo getRegisterContent() {
        return new RegisterContentPojo(timestamp, id, value);
    }

    @Override
    public void updateRegister(int timestamp, int id, float value) {
        if (timestamp > this.timestamp) {
            if (timestamp - this.timestamp > 1)
                logger.warn("Timestamp difference is {}, possible message loss or out of order delivery.",
                        timestamp - this.timestamp);
            updateRegisterState(timestamp, id, value);
        } else if (timestamp == this.timestamp && id < this.id) {
            logger.debug("Received update with the same timestamp as current but lower writer id ({})", id);
            updateRegisterState(timestamp, id, value);
        } else {
            logger.warn("Received outdated timestamp: {}, current timestamp: {}", timestamp, this.timestamp);
        }
    }

    private void updateRegisterState(int timestamp, int id, float value) {
        logger.trace("Updating register with timestamp: {}, id {}, and value: {}", timestamp, id, value);
        this.timestamp = timestamp;
        this.id = id;
        this.value = value;
    }
}
