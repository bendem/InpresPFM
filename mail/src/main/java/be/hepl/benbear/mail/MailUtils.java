package be.hepl.benbear.mail;

import be.hepl.benbear.commons.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

public final class MailUtils {

    private MailUtils() {}

    public static Mail from(Message m) throws MessagingException, IOException {
        Map<String, byte[]> attached = new HashMap<>();
        String text;

        Log.d("Content-Type: %s", m.getContentType());

        Mail.Type type = m.getContentType().contains("multipart/alternative") ? Mail.Type.HTML : Mail.Type.TEXT;

        if(m.getContent() instanceof Multipart) {
            Multipart multipart = (Multipart) m.getContent();
            if(m.getContentType().contains("multipart/mixed")) {
                extractAttachments(multipart, attached);
                text = lastContent(multipart);
            } else {
                text = lastContent(multipart);
            }
        } else {
            text = m.getContent().toString();
        }

        return new Mail(
            Arrays.stream(m.getFrom()).map(Address::toString).collect(Collectors.toList()),
            Arrays.stream(m.getAllRecipients()).map(Address::toString).collect(Collectors.toList()),
            m.getSubject(),
            type, text, attached,
            m.getSentDate() == null ? null : m.getSentDate().toInstant(),
            m.getReceivedDate() == null ? null : m.getReceivedDate().toInstant()
        );
    }

    private static void extractAttachments(Multipart multipart, Map<String, byte[]> attachments) throws MessagingException, IOException {
        for(int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                attachments.put(bodyPart.getFileName(), read(bodyPart));
            } else if(bodyPart.getContent() instanceof Multipart) {
                extractAttachments((Multipart) bodyPart.getContent(), attachments);
            }
        }
    }

    private static byte[] read(BodyPart bodyPart) throws IOException, MessagingException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = bodyPart.getInputStream();
        byte[] buffer = new byte[1024];
        int read;
        while((read = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toByteArray();
    }

    private static String lastContent(Multipart multipart) throws MessagingException, IOException {
        String content = "";
        for(int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                continue;
            }

            if(bodyPart.getContent() instanceof Multipart) {
                content = lastContent((Multipart) bodyPart.getContent());
            } else if(bodyPart.getContent() instanceof CharSequence) {
                content = bodyPart.getContent().toString();
            }
        }
        return content;
    }

}
