package home.blackharold.blacknour.protocol.impl;

import home.blackharold.blacknour.exception.NourRuntimeException;
import home.blackharold.blacknour.model.Command;
import home.blackharold.blacknour.model.Request;
import home.blackharold.blacknour.protocol.RequestConvertor;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DefaultRequestConverter extends AbstractPackageConverter implements RequestConvertor {

    @Override
    public Request readRequest(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        checkProtocolVersion(dataInputStream.readByte());
        byte cmd = dataInputStream.readByte();
        byte flag = dataInputStream.readByte();
        boolean hasKey = (flag & 1) != 0;
        boolean hasTtl = (flag & 2) != 0;
        boolean hasData = (flag & 3) != 0;
        return readRequest(cmd, hasKey, hasTtl, hasData, dataInputStream);
    }

    public Request readRequest(byte cmd, boolean hasKey, boolean hasTtl, boolean hasData, DataInputStream dataInputStream) throws IOException {
        Request request = new Request(Command.valueOf(cmd));
        if (hasKey) {
            byte keyLength = dataInputStream.readByte();
            byte[] keyBytes = IOUtils.readFully(dataInputStream, keyLength);
            request.setKey(new String(keyBytes, StandardCharsets.US_ASCII));
        }
        if (hasTtl) {
            request.setTtl(dataInputStream.readLong());
        }
        if (hasData) {
            int dataLength = dataInputStream.readInt();
            request.setData(IOUtils.readFully(dataInputStream, dataLength));
        }
        return request;
    }

    @Override
    public void writeRequest(OutputStream outputStream, Request request) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeByte(getVersionByte());
        dataOutputStream.writeByte(request.getCommand().getByteCode());
        dataOutputStream.writeByte(getFlagsByte(request));
        if (request.hasKey()) {
            writeKey(dataOutputStream, request);
        }
        if (request.hasTtl()) {
            dataOutputStream.writeLong(request.getTtl());
        }

        if (request.hasData()) {
            dataOutputStream.writeInt(request.getData().length);
            dataOutputStream.write(request.getData());
        }
        dataOutputStream.flush();
    }

    //Flags: 0000 0001, 0000 0101, 0000 0111
    public byte getFlagsByte(Request request) {
        byte flags = 0;
        if (request.hasKey()) {
            flags |= 1;
        }
        if (request.hasTtl()) {
            flags |= 2;
        }
        if (request.hasData()) {
            flags |= 4;
        }
        return flags;
    }

    public void writeKey(DataOutputStream dataOutputStream, Request request) throws IOException {
        byte[] key = request.getKey().getBytes(StandardCharsets.US_ASCII);
        if (key.length > 127) {
            throw new NourRuntimeException("Key length should be <= 127 bytes for key: " + request.getKey());
        }
        dataOutputStream.writeByte(key.length);
        dataOutputStream.write(key);
    }
}
