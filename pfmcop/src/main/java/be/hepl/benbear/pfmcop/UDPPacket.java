package be.hepl.benbear.pfmcop;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UDPPacket {

    public static byte[] digest(String from, String content, UUID uuid) {
        byte[] fromBytes = from.getBytes();
        byte[] contentBytes = content.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(fromBytes.length + contentBytes.length + 36); // 36 = UUIDv4 size
        bb
            .put(fromBytes)
            .put(contentBytes)
            .put(uuid.toString().getBytes())
            .flip();
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
