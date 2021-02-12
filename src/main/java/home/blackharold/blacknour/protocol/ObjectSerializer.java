package home.blackharold.blacknour.protocol;

public interface ObjectSerializer {
    byte[] toByteArray(Object o);

    Object fromByteArray(byte[] array);
}
