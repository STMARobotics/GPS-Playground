package frc.robot.subsystems.gps.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * UBXMessageTest
 */
public class UBXMessageTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testNewMessageNoData() {
        UBXMessage message = new UBXMessage((byte)0x0A, (byte)0x04);
        byte[] result = message.createMessage();

        byte[] expected = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x00, 0x00, 0x0E, 0x34};

        assertArrayEquals(expected, result);
    }

    @Test
    public void testNewMessageWithData() {
        UBXMessage message = new UBXMessage((byte)0x0A, (byte)0x04, new byte[] {0x01, 0x02, 0x03});
        byte[] result = message.createMessage();

        byte[] expected = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x00, 0x03, 0x01, 0x02, 0x03, 0x17, 0x74};

        assertArrayEquals(expected, result);
    }

    @Test
    public void testParseMessageNoData() {
        byte[] rawMessage = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x00, 0x00, 0x0E, 0x34};
        UBXMessage message = UBXMessage.parseMessage(rawMessage);

        assertEquals((byte) 0x0A, message.getClazz());
        assertEquals((byte) 0x04, message.getId());
        assertEquals(0, message.getPayload().length);
    }

    @Test
    public void testParseMessageWithData() {
        byte[] rawMessage = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x00, 0x03, 0x01, 0x02, 0x03, 0x17, 0x74};
        UBXMessage message = UBXMessage.parseMessage(rawMessage);

        assertEquals((byte) 0x0A, message.getClazz());
        assertEquals((byte) 0x04, message.getId());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, message.getPayload());
    }

    @Test
    public void testParseMessageMissingSync() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid sync character(s)");

        byte[] rawMessage = new byte[]{0x0A, 0x04, 0x00, 0x03, 0x01, 0x02, 0x03, 0x17, 0x74};
        UBXMessage.parseMessage(rawMessage);
    }

    @Test
    public void testParseMessageInvalidPayloadLength() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid payload length. Expected: 259 Actual: 1");

        byte[] rawMessage = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x01, 0x03, 0x01, 0x00, 0x00};
        UBXMessage.parseMessage(rawMessage);
    }

    @Test
    public void testParseMessageInvalidChecksum() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid checksum");

        byte[] rawMessage = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x00, 0x03, 0x01, 0x02, 0x03, 0x17, 0x75};
        UBXMessage.parseMessage(rawMessage);
    }
}