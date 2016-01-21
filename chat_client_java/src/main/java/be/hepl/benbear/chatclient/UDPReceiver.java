package be.hepl.benbear.chatclient;

import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.serialization.ObjectSerializer;
import be.hepl.benbear.pfmcop.UDPPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UDPReceiver implements Runnable {

    public static final int PACKET_MAX_SIZE = 1024;

    private final MulticastSocket socket;
    private final SocketAddress address;
    private final Consumer<Message> messageConsumer;
    private final Supplier<Boolean> shouldStop;
    private final BinarySerializer serializer;

    public UDPReceiver(MulticastSocket socket, SocketAddress address, Consumer<Message> messageConsumer, Supplier<Boolean> shouldStop) {
        this.socket = socket;
        this.address = address;
        this.messageConsumer = messageConsumer;
        this.shouldStop = shouldStop;
        serializer = BinarySerializer.getInstance();

        ObjectSerializer<UDPPacket> objectSerializer = new ObjectSerializer<>(UDPPacket.class);
        serializer.registerSerializer(UDPPacket.class, objectSerializer, objectSerializer);
        serializer.registerSerializer(
            UDPPacket.Type.class,
            (o, dos) -> dos.writeInt(o.ordinal()),
            bb -> UDPPacket.Type.values()[bb.getInt()]);
    }

    @Override
    public void run() {
        DatagramPacket packet = new DatagramPacket(new byte[PACKET_MAX_SIZE], 0, PACKET_MAX_SIZE, address);
        int length;
        byte[] data;

        while(!shouldStop.get()) {
            try {
                socket.receive(packet);
            } catch(IOException e) {
                Log.e("Failed to receive packet", e);
                break;
            }
            data = packet.getData();

            length = data[0] << 8 | data[1];
            if(length > PACKET_MAX_SIZE) {
                Log.e("Dropping packet too large...");
                continue;
            }

            Log.d("Received %d bytes: %s", length, Arrays.toString(Arrays.copyOfRange(packet.getData(), 2, length + 2)));

            UDPPacket deserialized = serializer.deserialize(UDPPacket.class, ByteBuffer.wrap(packet.getData(), 2, length));

            if(deserialized.type == UDPPacket.Type.QUESTION && !Arrays.equals(deserialized.digest,
                    UDPPacket.digest(deserialized.from, deserialized.content, deserialized.tag))) {
                Log.w("Invalid digest, dropping question from %s: %s", deserialized.from, deserialized.content);
                continue;
            }
            messageConsumer.accept(Message.from(deserialized));
        }
    }

}
