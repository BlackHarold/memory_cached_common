package home.blackharold.blacknour.protocol.impl;

import home.blackharold.blacknour.model.Response;
import home.blackharold.blacknour.model.Status;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DefaultResponseConverterTest {

    private final DefaultResponseConverter defaultResponseConverter = new DefaultResponseConverter();

    @Test
    public void readResponseWithoutData() throws IOException {
        Response response = defaultResponseConverter.readResponse(new ByteArrayInputStream(
                new byte[]{
                        16, //version
                        0,  //status
                        0   //flag
                }));
        Assert.assertEquals(Status.ADDED, response.getStatus());
        Assert.assertFalse(response.hasData());
    }

    @Test
    public void readResponseWithData() throws IOException {
        Response response = defaultResponseConverter.readResponse(new ByteArrayInputStream(
                new byte[]{
                        16, //version
                        0,  //status
                        1,  //flag that has data
                        0, 0, 0, 3, //int length
                        1, 2, 3 //data
                }));
        Assert.assertEquals(Status.ADDED, response.getStatus());
        Assert.assertTrue(response.hasData());
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, response.getData());
    }

    @Test
    public void writeResponseWithoutData() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = new Response(Status.GOTTEN);
        defaultResponseConverter.writeResponse(outputStream, response);
        Assert.assertArrayEquals(new byte[]{16, 2, 0}, outputStream.toByteArray());
    }

    @Test
    public void writeResponseWithData() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = new Response(Status.ADDED, new byte[]{1, 2, 3});
        defaultResponseConverter.writeResponse(outputStream, response);
        Assert.assertArrayEquals(new byte[]{
                16, //version
                0,  //status
                1,  //flag that has data
                0, 0, 0, 3, //int length
                1, 2, 3 //data
        }, outputStream.toByteArray());
    }
}
