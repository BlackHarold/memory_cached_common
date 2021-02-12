package home.blackharold.blacknour.model;

import home.blackharold.blacknour.exception.NourRuntimeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class VersionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void valueOfSuccess() {
        assertEquals(Version.VERSION_1_0, Version.valueOf((byte) 16));
    }

    @Test
    public void valueOfFailed() {
        thrown.expect(NourRuntimeException.class);
        thrown.expectMessage(is("Unsupported version: 127"));
        assertEquals("Unsupported protocol version: 127", Version.valueOf(Byte.MAX_VALUE).toString());
    }

}
