package frc.robot.subsystems.gps.messages;

import static java.util.Arrays.copyOfRange;

public class UBXMessage {

    private static final byte SYNC_CHAR_1 = (byte) 0xB5;
    private static final byte SYNC_CHAR_2 = 0x62;
    private static final int INDEX_SYNC1 = 0;
    private static final int INDEX_SYNC2 = 1;
    private static final int INDEX_CLASS = 2;
    private static final int INDEX_ID = 3;
    private static final int INDEX_PAYLOAD_SIZE_HIGH = 4;
    private static final int INDEX_PAYLOAD_SIZE_LOW = 5;
    private static final int INDEX_PAYLOAD_START = 6;

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

    public byte getClazz() {
        return clazz;
    }

    public byte getId() {
        return id;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] createMessage() {
        byte[] message = new byte[8 + payload.length];
        message[INDEX_SYNC1] = SYNC_CHAR_1;
        message[INDEX_SYNC2] = SYNC_CHAR_2;
        message[INDEX_CLASS] = clazz;
        message[INDEX_ID] = id;
        message[INDEX_PAYLOAD_SIZE_HIGH] = (byte) ((payload.length >> 8) & 0xFF);
        message[INDEX_PAYLOAD_SIZE_LOW] = (byte) (payload.length & 0xFF);
        if (payload == null || payload.length == 0) {
            message[INDEX_PAYLOAD_START] = 0;
        } else {
            System.arraycopy(payload, 0, message, INDEX_PAYLOAD_START, payload.length);
        }

        byte[] checksum = calculateChecksum(copyOfRange(message, 2, message.length - 2));

        message[message.length - 2] = checksum[0];
        message[message.length - 1] = checksum[1];

        return message;
    }

    private static byte[] calculateChecksum(byte[] data) {
        int ckA = 0;
        int ckB = 0;

        for (int i = 0; i < data.length; i++) {
            ckA += data[i] & 0xFF;
            ckB += ckA;
        }
        return new byte[] { (byte) (ckA & 0xFF), (byte) (ckB & 0xFF) };
    }

    public static UBXMessage parseMessage(byte[] rawData) {
        if (rawData[INDEX_SYNC1] != SYNC_CHAR_1 || rawData[INDEX_SYNC2] != SYNC_CHAR_2) {
            throw new RuntimeException("Invalid sync character(s)");
        }

        int payloadSize = (rawData[INDEX_PAYLOAD_SIZE_HIGH] & 0XFF) << 8 | (rawData[INDEX_PAYLOAD_SIZE_LOW] & 0xFF);

        if (payloadSize != rawData.length - 8) {
            throw new RuntimeException("Invalid payload length. Expected: " + payloadSize + " Actual: " + (rawData.length - 8));
        }

        byte[] checksum = calculateChecksum(copyOfRange(rawData, 2, rawData.length - 2));
        if (checksum[0] != rawData[rawData.length - 2] || checksum[1] != rawData[rawData.length - 1]) {
            throw new RuntimeException("Invalid checksum");
        }

        return new UBXMessage(
            rawData[INDEX_CLASS], rawData[INDEX_ID], copyOfRange(rawData, INDEX_PAYLOAD_START, rawData.length - 2));
    }
    
}
