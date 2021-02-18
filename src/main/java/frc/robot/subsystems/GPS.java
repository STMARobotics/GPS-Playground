package frc.robot.subsystems;

import java.io.Closeable;
import java.io.IOException;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import net.sf.marineapi.nmea.parser.SentenceFactory;

public class GPS implements Closeable {

    private final I2C i2c;
    private final Thread thread;
    private boolean stop = false;

    public GPS() {
        i2c = new I2C(Port.kOnboard, 0x42);

        // TODO - Try removing the thread and using TimedRobot.addPeriodic
        thread = new Thread(() -> {
            byte[] highByte = new byte[1];
            byte[] lowByte = new byte[1];
            while (!stop) {
                try {
                    i2c.read(0xFD, 1, highByte);
                    i2c.read(0xFE, 1, lowByte);
                    int highUint = Byte.toUnsignedInt(highByte[0]);
                    int lowUint = Byte.toUnsignedInt(lowByte[0]);

                    int bytesAvailable = (short) highUint << 8 | lowUint;
                    // System.out.println("Bytes available: " + bytesAvailable + " > " + highUint + ", " + lowUint);
                    if (bytesAvailable <= 0 || highUint == 0x7F || highUint == 0xFF || lowUint == 0x7F ||lowUint == 0xFF) {
                       // No data right now

                    } else {
                        // System.out.println("Bytes Available: " + bytesAvailable);
                        byte[] buffer = new byte[1];
                        i2c.readOnly(buffer, 1);
                        int chara = Byte.toUnsignedInt(buffer[0]);
                        if (chara != 0xFF) {
                            // No data
                            // String stringData = new String(chara, StandardCharsets.ISO_8859_1);
                            System.out.println("Read String: " + chara);
                        }
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
    
}
