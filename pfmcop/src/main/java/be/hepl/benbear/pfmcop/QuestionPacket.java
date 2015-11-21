package be.hepl.benbear.pfmcop;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class QuestionPacket extends UDPPacket {

    public static byte[] digest(String from, String content, UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(from.length() + content.length() + 36);
        bb.put(from.getBytes()).put(content.getBytes()).put(uuid.toString().getBytes());
        bb.flip();
        return Digestion.digest(bb);
    }

    private final byte[] digest;

    public QuestionPacket(String from, String content, UUID tag, byte[] digest) {
        super(Type.QUESTION, from, content, tag);
        this.digest = digest;
    }

    public boolean checkDigest(byte[] digest) {
        return Arrays.equals(this.digest, digest);
    }

}
