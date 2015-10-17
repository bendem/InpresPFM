package be.hepl.benbear.trafficserver;

import be.hepl.benbear.commons.net.BinarySerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        byte[] bytes = BinarySerializer.getInstance().serialize(new String[]{"hey", "oh", "a", ""});
        String[] arr = BinarySerializer.getInstance().deserialize(String[].class, ByteBuffer.wrap(bytes));
        System.out.println(Arrays.toString(arr));

        bytes = BinarySerializer.getInstance().serialize(new int[]{3, 2, -4});
        int[] ints = BinarySerializer.getInstance().deserialize(int[].class, ByteBuffer.wrap(bytes));
        System.out.println(Arrays.toString(ints));
    }

}
