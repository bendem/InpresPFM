package be.hepl.benbear.trafficserver;

import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.protocol.ProtocolHandler;
import be.hepl.benbear.protocol.tramap.InputLorryPacket;
import be.hepl.benbear.protocol.tramap.PacketId;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        BinarySerializer serializer = BinarySerializer.getInstance();
        byte[] bytes = serializer.serialize(new String[]{"hey", "oh", "a", ""});
        String[] arr = serializer.deserialize(String[].class, ByteBuffer.wrap(bytes));
        System.out.println(Arrays.toString(arr));

        bytes = serializer.serialize(new int[]{3, 2, -4});
        int[] ints = serializer.deserialize(int[].class, ByteBuffer.wrap(bytes));
        System.out.println(Arrays.toString(ints));

        ProtocolHandler handler = new ProtocolHandler();
        handler.registerPacket(PacketId.InputLorry.id, InputLorryPacket.class);
        bytes = serializer.serialize(new InputLorryPacket("reservation", new String[] {
            "id1", "id2"
        }));
        InputLorryPacket inputLorryPacket = serializer.deserialize(InputLorryPacket.class, ByteBuffer.wrap(bytes));
        System.out.println(inputLorryPacket.getReservationId() + ' ' + inputLorryPacket.getContainerIds());
    }

}
