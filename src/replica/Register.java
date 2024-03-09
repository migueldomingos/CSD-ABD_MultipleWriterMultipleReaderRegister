package replica;

public interface Register {

    int getTimestamp();

    float getValue();

    void updateRegister(int timestamp, float value);

}
