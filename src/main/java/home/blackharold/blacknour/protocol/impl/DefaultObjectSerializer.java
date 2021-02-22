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
        if (!(o instanceof Serializable)) {
            throw new NourRuntimeException("Class " + o.getClass().getName() + " should implement java.io.Serializable interface");
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(o);
            out.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new NourRuntimeException("Can't convert object to byte array: " + e.getMessage(), e);
        }
    }

    @Override
    public Object fromByteArray(byte[] array) {
        if (array == null) {
            return null;
        }
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(array));
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new NourRuntimeException("Can't get object from byte array: " + e.getMessage(), e);
        }
    }
}
