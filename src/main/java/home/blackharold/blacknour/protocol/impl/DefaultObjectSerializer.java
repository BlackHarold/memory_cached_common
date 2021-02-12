package home.blackharold.blacknour.protocol.impl;

import home.blackharold.blacknour.exception.NourRuntimeException;
import home.blackharold.blacknour.protocol.ObjectSerializer;

import java.io.*;

public class DefaultObjectSerializer implements ObjectSerializer {
    @Override
    public byte[] toByteArray(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Serializable) {
            throw new NourRuntimeException("Class " + o.getClass().getName() + " should implement java.io.Serializable interface");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new NourRuntimeException("can't convert object to byte array: " + e.getMessage(), e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object fromByteArray(byte[] array) {
        if (array == null) {
            return null;
        }
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(array));
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new NourRuntimeException("Can't get object from byte array: " + e.getMessage(), e);
        }
    }
}
