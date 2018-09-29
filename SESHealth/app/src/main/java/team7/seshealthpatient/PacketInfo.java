package team7.seshealthpatient;

public class PacketInfo {

    private String key;
    private String info;

    public PacketInfo(String key, String info) {
        this.key = key;
        this.info = info;
    }

    public String getkey() {
        return key;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "[" + key + "]\n" + info;
    }
}
