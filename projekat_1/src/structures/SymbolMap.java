package structures;

public class SymbolMap {
    private byte value;
    private String code;

    public SymbolMap(byte value, String code) {
        this.value = value;
        this.code = code;
    }

    public byte getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
