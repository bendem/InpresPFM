package be.hepl.benbear.pfmcop;

import java.util.UUID;

public class UDPPacket {

    public enum Type {
        QUESTION, ANSWER, EVENT
    }

    public final Type type;
    public final String from;
    public final String content;
    public final UUID tag;

    public UDPPacket(Type type, String from, String content, UUID tag) {
        this.type = type;
        this.from = from;
        this.content = content;
        this.tag = tag;
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
