package home.blackharold.blacknour.model;

import home.blackharold.blacknour.exception.NourRuntimeException;

public enum Version {
    VERSION_0_0(0, 0),
    VERSION_0_1(0, 1),
    VERSION_1_0(1, 0);

    private byte version;
    private byte subversion;

    Version(int version, int subversion) {
        this.version = (byte) (version & 0x7);
        this.subversion = (byte) (subversion & 0xF);
    }

    public static Version valueOf(byte byteCode) {
        for (Version version : Version.values()) {
            if (version.getByteCode() == byteCode) {
                return version;
            }
        }
        throw new NourRuntimeException("Unsupported version: " + byteCode);
    }

    public byte getByteCode() {
        return (byte) (subversion + (version << 4));
    }

    @Override
    public String toString() {
        return String.format("%s.%s", version, subversion);
    }
}
