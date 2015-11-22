package be.hepl.benbear.pfmcop;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UDPPacket {

    public static byte[] digest(String from, String content, UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(from.length() + content.length() + 36);
        bb.put(from.getBytes()).put(content.getBytes()).put(uuid.toString().getBytes());
        bb.flip();
        return Digestion.digest(bb);
    }

    public enum Type {
        QUESTION, ANSWER, EVENT
    }

    public final Type type;
    public final String from;
    public final String content;
    public final UUID tag;
    public final byte[] digest;

    public UDPPacket(Type type, String from, String content, UUID tag, byte[] digest) {
        this.type = type;
        this.from = from;
        this.content = content;
        this.tag = tag;
        this.digest = digest;
    }

    public Type getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public UUID getTag() {
        return tag;
    }

}
