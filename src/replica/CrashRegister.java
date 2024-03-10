package replica;

import lombok.SneakyThrows;
import pojos.RegisterContentPojo;

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
