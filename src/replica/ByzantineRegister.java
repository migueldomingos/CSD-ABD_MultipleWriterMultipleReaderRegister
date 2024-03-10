package replica;

import pojos.RegisterContentPojo;

/**
 * Simulates a register that is faulty and responds with incorrect values.
 * It always returns the same value and id, and a timestamp of Integer.MAX_VALUE.
 * A more insidious attack would be to return a correct timestamp and id, but a faulty value.
 */
public class ByzantineRegister implements Register {

    private static final float FAULTY_VALUE = Float.MAX_VALUE;

    private static final int FAULTY_ID = 0;

    private static final int FAULTY_TIMESTAMP = Integer.MAX_VALUE;

    @Override
    public RegisterContentPojo getRegisterContent() {
        return new RegisterContentPojo(FAULTY_TIMESTAMP, FAULTY_ID, FAULTY_VALUE);
    }

    @Override
    public void updateRegister(int timestamp, int id, float value) {
        // Uhm, I don't think so...
    }

}
