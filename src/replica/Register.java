package replica;

import pojos.RegisterContentPojo;

public interface Register {

    RegisterContentPojo getRegisterContent();

    void updateRegister(int timestamp, int id, float value);

}
