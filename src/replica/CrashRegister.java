package replica;

import lombok.SneakyThrows;

public class CrashRegister implements Register {

    @Override
    @SneakyThrows
    public int getTimestamp() {
        Thread.sleep(Integer.MAX_VALUE);
        return 0;
    }

    @Override
    @SneakyThrows
    public float getValue() {
        Thread.sleep(Integer.MAX_VALUE);
        return 0;
    }

    @Override
    @SneakyThrows
    public void updateRegister(int timestamp, float value) {
        Thread.sleep(Integer.MAX_VALUE);
    }

}
