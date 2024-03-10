package replica;

import lombok.SneakyThrows;
import pojos.RegisterContentPojo;

/**
 * Simulates a register that crashes after receiving a request
 * The register will not respond to the request and will not update its state
 */
public class CrashRegister implements Register {

    @Override
    @SneakyThrows
    public RegisterContentPojo getRegisterContent() {
        Thread.sleep(Integer.MAX_VALUE);
        return null;
    }

    @Override
    @SneakyThrows
    public void updateRegister(int timestamp, int id, float value) {
        Thread.sleep(Integer.MAX_VALUE);
    }

}
