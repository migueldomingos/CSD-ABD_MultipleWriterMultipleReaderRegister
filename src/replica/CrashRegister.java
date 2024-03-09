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
    public void setTimestamp(int timestamp) {
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    @SneakyThrows
    public void setValue(float value) {
        Thread.sleep(Integer.MAX_VALUE);
    }
}
