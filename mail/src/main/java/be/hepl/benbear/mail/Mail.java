package be.hepl.benbear.mail;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Mail {

    public enum Type {
        HTML, TEXT
    }

    private final List<String> from;
    private final List<String> to;
    private final String subject;
    private final Type type;
    private final String content;
    private final Map<String, byte[]> attached;
    private final Instant sent;
    private final Instant received;

    public Mail(List<String> from, List<String> to, String subject, Type type, String content, Map<String, byte[]> attached, Instant sent, Instant received) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.type = type;
        this.content = content;
        this.attached = attached;
        this.sent = sent;
        this.received = received == null ? Instant.now() : received;
    }

    public List<String> getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public Type getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Map<String, byte[]> getAttached() {
        return attached;
    }

    public Optional<Instant> getSent() {
        return Optional.ofNullable(sent);
    }

    public Optional<Instant> getReceived() {
        return Optional.ofNullable(received);
    }

}
