package frc.robot.subsystems;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import frc.robot.subsystems.gps.messages.UBXMessage;

public class GPS implements Closeable {

    private final I2C i2c;
    private final Thread thread;
    private boolean stop = false;

    public GPS() {
        i2c = new I2C(Port.kOnboard, 0x42);

        // TODO - Try removing the thread and using TimedRobot.addPeriodic
        thread = new Thread(() -> {

            UBXMessage monVerMsg = new UBXMessage((byte)0x0A, (byte)0x04);
            i2c.writeBulk(monVerMsg.createMessage());
            
            byte[] bytesAvail = new byte[2];
            while (!stop) {
                try {
                    i2c.read(0xFD, 2, bytesAvail);

                    int bytesAvailable = (bytesAvail[0] & 0xFF) << 8 | (bytesAvail[1] & 0xFF);
                    // System.out.println("Bytes available: " + bytesAvailable);
                    if (bytesAvailable > 0) {
                        System.out.print("Bytes Available: " + bytesAvailable);
                        byte[] buffer = new byte[100];
                        int len = Math.min(buffer.length, bytesAvailable);
                        if (i2c.read(0xFF, len, buffer)) {
                            System.err.println("Failed to read from device");
                        } else {
                            System.out.println(" - Read " + len + " bytes");
                            System.out.println(new String(buffer, StandardCharsets.ISO_8859_1));
                        }
                    } else {
                        System.out.println("No bytes available");
                    }
                    Thread.sleep(25);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "GPS");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void close() throws IOException {
        stop = true;
    }

    byte[] toByteArray(int[] array) {
        byte[] result = new byte[array.length];
        for(int i = 0; i < array.length; i++) {
            result[i] = (byte)array[i];
        }
        return result;
    }
    
}
