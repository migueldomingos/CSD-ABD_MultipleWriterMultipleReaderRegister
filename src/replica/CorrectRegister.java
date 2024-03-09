package replica;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CorrectRegister implements Register {

    private float value;

    private int timestamp;

    public CorrectRegister() {
        value = -1;
        timestamp = -1;
    }

}
