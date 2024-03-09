package replica;

public class RegisterSingleton {

    public enum RegisterType {
        CORRECT, CRASH, BYZANTINE
    }

    private static Register register = null;

    public static void setRegister(RegisterType type) throws Exception {
        if (RegisterSingleton.register != null)
            throw new Exception("Register already set");
        switch (type) {
            case CORRECT:
                register = new CorrectRegister();
                break;
            case CRASH:
                register = new CrashRegister();
                break;
            case BYZANTINE:
                register = new ByzantineRegister();
                break;
            default:
                throw new Exception("Invalid register type");
        }
    }

    public static Register getRegister() throws Exception {
        if (register == null)
            throw new Exception("Register not set");
        return register;
    }

}
