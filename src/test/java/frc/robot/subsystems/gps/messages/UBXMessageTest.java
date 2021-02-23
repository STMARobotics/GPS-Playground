package frc.robot.subsystems.gps.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * UBXMessageTest
 */
public class UBXMessageTest {

    @Test
    public void testMessage() {
        UBXMessage message = new UBXMessage((byte)0x0A, (byte)0x04);
        byte[] result = message.createMessage();

        byte[] expected = new byte[]{(byte)0xB5, 0x62, 0x0A, 0x04, 0x00, 0x00, 0x0E, 0x34};

        System.out.println(Arrays.toString(expected));
        System.out.println(Arrays.toString(result));
        assertArrayEquals(expected, result);
    }
    
}