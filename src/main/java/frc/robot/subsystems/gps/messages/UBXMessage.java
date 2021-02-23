package frc.robot.subsystems.gps.messages;

public class UBXMessage {

    private final byte syncChar1 = (byte) 0xB5;

    private final byte syncChar2 = 0x62;

    private byte clazz;

    private byte id;

    private byte[] payload;

    public UBXMessage(byte clazz, byte id, byte[] payload) {
        this.clazz = clazz;
        this.id = id;
        this.payload = payload;
    }

    public UBXMessage(byte clazz, byte id) {
        this.clazz = clazz;
        this.id = id;
        this.payload = new byte[0];
    }

    public byte[] createMessage() {
        byte[] message = new byte[7 + (payload.length == 0 ? 1 : payload.length)];
        message[0] = syncChar1;
        message[1] = syncChar2;
        message[2] = clazz;
        message[3] = id;
        message[4] = payload == null ? 0 : (byte)payload.length;
        if (payload == null || payload.length == 0) {
            message[5] = 0;
        } else {
            System.arraycopy(payload, 0, message, 5, payload.length);
        }

        int ckA = 0;
        int ckB = 0;

        for(int i = 2; i < message.length - 2; i++) {
            ckA += message[i] & 0xFF;
            ckB += ckA;
        }

        message[message.length - 2] = (byte) (ckA & 0xFF);
        message[message.length - 1] = (byte) (ckB & 0xFF);

        return message;
    }
    
}
