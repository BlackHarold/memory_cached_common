package home.blackharold.blacknour.protocol.impl;

import home.blackharold.blacknour.exception.NourRuntimeException;
import home.blackharold.blacknour.model.Command;
import home.blackharold.blacknour.model.Request;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultRequestConverterTest {
    private final DefaultRequestConverter defaultRequestConverter = new DefaultRequestConverter();

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void getFlagsByteEmpty() {
        Request request = new Request(Command.CLEAR);
        byte flags = defaultRequestConverter.getFlagsByte(request);
        assertEquals(0, flags);
    }

    @Test
    public void getFlagsByteAll() {
        Request request = new Request(Command.CLEAR, "key", System.currentTimeMillis(), new byte[]{1});
        byte flags = defaultRequestConverter.getFlagsByte(request);
        assertEquals(7, flags);
    }

    @Test
    public void writeKeySuccess() throws IOException {
        DataOutputStream dataOutputStream = spy(new DataOutputStream(mock(OutputStream.class)));
        String key = "key";
        defaultRequestConverter.writeKey(dataOutputStream, new Request(Command.GET, key));

        verify(dataOutputStream).write((key.getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    public void writeKeyFailed() throws IOException {
        String key = StringUtils.repeat("a", 128);
        thrown.expect(NourRuntimeException.class);
        thrown.expectMessage(is("Key length should be <= 127 bytes for key: " + key));
        DataOutputStream dataOutputStream = new DataOutputStream(null);
        defaultRequestConverter.writeKey(dataOutputStream, new Request(Command.CLEAR, key));
    }

    @Test
    public void readRequestWithoutData() throws IOException {
        Request request = defaultRequestConverter.readRequest(new ByteArrayInputStream(new byte[]{
                16,     //version
                0,      //command
                0       //flag
        }));
        assertEquals(Command.CLEAR, request.getCommand());
        assertFalse(request.hasKey());
        assertFalse(request.hasTtl());
        assertFalse(request.hasData());
    }

    @Test
    public void readRequestWithData() throws IOException {
        Request request = defaultRequestConverter.readRequest(new ByteArrayInputStream(new byte[]{
                16,                         //version
                1,                          //command
                7,                          //flag that has data
                3,                          //int length
                49, 50, 51,                 //key bytes
                0, 0, 0, 0, 0, 0, 0, 5,     //ttl
                0, 0, 0, 3,                 //data length
                1, 2, 3                     //data
        }));
        assertEquals(Command.PUT, request.getCommand());
        assertTrue(request.hasKey());
        assertEquals("123", request.getKey());
        assertTrue(request.hasTtl());
        assertEquals(Long.valueOf(5L), request.getTtl());
        assertTrue(request.hasData());
        assertArrayEquals(new byte[]{1, 2, 3}, request.getData());
    }

    @Test
    public void writeRequestWithoutData() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        defaultRequestConverter.writeRequest(outputStream, new Request(Command.CLEAR));
        assertArrayEquals(new byte[]{
                16,     //version
                0,      //command
                0       //flag
        }, outputStream.toByteArray());
    }

    @Test
    public void writeRequestWithData() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        defaultRequestConverter.writeRequest(outputStream, new Request(Command.PUT, "123", 5L, new byte[]{1, 2, 3}));
        assertArrayEquals(new byte[]{
                16,                         //version
                1,                          //command
                7,                          //flag that has data
                3,                          //int length
                49, 50, 51,                 //key bytes
                0, 0, 0, 0, 0, 0, 0, 5,     //ttl
                0, 0, 0, 3,                 //data length
                1, 2, 3                     //data
        }, outputStream.toByteArray());
    }
}
