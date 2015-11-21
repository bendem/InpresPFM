package be.hepl.benbear.chatclient;

import be.hepl.benbear.pfmcop.UDPPacket;

import java.util.UUID;

public class Message {

    public final UDPPacket.Type type;
    public final String username;
    public final String message;
    public final UUID uuid;

    public Message(UDPPacket.Type type, String username, String message, UUID uuid) {
        this.type = type;
        this.username = username;
        this.message = message;
        this.uuid = uuid;
    }

    public UDPPacket.Type getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }

}
