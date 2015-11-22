package be.hepl.benbear.commons.protocol;

import be.hepl.benbear.commons.checking.Sanity;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.serialization.ObjectSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

// TODO Events for each packet and connection closed
public class ProtocolHandler {

    private static final byte FRAME_END = 0x42;

    private final Map<Byte, Class<? extends Packet>> packetsById;
    private final BinarySerializer serializer;

    public ProtocolHandler() {
        packetsById = new HashMap<>();
        serializer = BinarySerializer.getInstance();
    }

    public synchronized <T extends Packet> ProtocolHandler registerPacket(byte id, Class<T> packetClass) {
        Sanity.noneNull(packetClass);

        if(packetsById.containsKey(id)) {
            throw new IllegalStateException("A packet with id = " + id + " or class = " + packetClass.getName() + " is already registered");
        }

        packetsById.put(id, packetClass);
        ObjectSerializer<T> objectSerializer = new ObjectSerializer<>(packetClass);
        serializer.registerSerializer(packetClass, objectSerializer, objectSerializer);
        return this;
    }

    public <T extends Packet> T read(InputStream is) throws IOException {
        Sanity.noneNull(is);

        byte[] b = accumulate(is, 3);
        int len = b[1] << 8 | b[2];

        Log.d("Received packet %d of length %d", b[0], len);

        Class<T> packetClass = (Class<T>) packetsById.get(b[0]);
        if(packetClass == null) {
            throw new ProtocolException("No mapping for packet id " + b[0]);
        }

        byte[] bytes = accumulate(is, len + 1);
        if(bytes[bytes.length - 1] != FRAME_END) {
            throw new ProtocolException("Invalid frame end");
        }

        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, bytes.length - 1);
        return serializer.deserialize(packetClass, bb);
    }

    public <T extends Packet> T readSpecific(InputStream is, Class<T> packetClass) throws IOException {
        Sanity.noneNull(is, packetClass);

        T read;
        while((read = read(is)).getClass() != packetClass);

        return read;
    }

    public <T extends Packet> ProtocolHandler write(OutputStream os, T packet) throws IOException {
        Sanity.noneNull(os, packet);

        byte[] bytes = serializer.serialize(packet);

        Log.d("Writing packet %d of length %d", packet.getId(), bytes.length);

        os.write(packet.getId());
        os.write(bytes.length >> 8 & 0xff);
        os.write(bytes.length & 0xff);
        os.write(bytes);
        os.write(FRAME_END);

        return this;
    }

    private byte[] accumulate(InputStream is, int len) throws IOException {
        byte[] b = new byte[len];
        int read = 0;
        while(read < len) {
            read += is.read(b, read, len - read);
        }
        return b;
    }

}
