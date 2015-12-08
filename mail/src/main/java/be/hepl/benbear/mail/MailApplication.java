package be.hepl.benbear.mail;

import be.hepl.benbear.commons.config.Config;
import be.hepl.benbear.commons.jfx.BaseApplication;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.streams.Predicates;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailApplication extends BaseApplication {

    private static final Flags DELETED_FLAG = new Flags(Flags.Flag.DELETED);

    private final ScheduledExecutorService threadPool;
    private final Config config;
    private MailController mailController;

    public MailApplication() {
        super(getResource("style.css"));
        threadPool = Executors.newScheduledThreadPool(1);
        config = new Config();
    }

    public CompletableFuture<Void> send(Map<Message.RecipientType, String[]> to, String subject, String content, Set<File> attachedFiles) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        threadPool.execute(UncheckedLambda.runnable(() -> {
            Properties props = new Properties();
            String smtpHost = config.getStringThrowing("mail.smtp.host");
            int smtpPort = config.getIntThrowing("mail.smtp.port");
            Optional<String> smtpUser = config.getString("mail.smtp.username");
            Optional<String> smtpPassword = config.getString("mail.smtp.password");
            InternetAddress from = new InternetAddress(config.getStringThrowing("mail.address"));

            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            if(smtpPassword.isPresent()) {
                props.put("mail.smtp.auth", smtpHost);
            }

            Session session = Session.getInstance(props);
            Message message = new MimeMessage(session);
            message.setFrom(from);
            message.setSubject(subject);
            message.setSentDate(new Date());

            if(!attachedFiles.isEmpty()) {
                fillMessage(message, content, attachedFiles);
            } else {
                fillMessage(message, content);
            }

            to.forEach(UncheckedLambda.biconsumer((type, addresses) -> message.addRecipients(
                type,
                Arrays.stream(addresses)
                    .map(UncheckedLambda.function(InternetAddress::new, Throwable::printStackTrace))
                    .filter(Predicates.notNull())
                    .toArray(InternetAddress[]::new)
            )));

            Transport transport = session.getTransport(config.getStringThrowing("mail.smtp.protocol"));
            transport.connect(smtpHost, smtpUser.orElse(null), smtpPassword.orElse(null));
            transport.sendMessage(message, message.getAllRecipients());
            future.complete(null);
        }, future::completeExceptionally));

        return future;
    }

    private Message fillMessage(Message message, String content) throws MessagingException {
        message.setText(content);
        return message;
    }

    private Message fillMessage(Message message, String content, Set<File> files) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        MimeBodyPart part = new MimeBodyPart();
        part.setText(content);
        multipart.addBodyPart(part);

        for(File attachedFile : files) {
            part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(new FileDataSource(attachedFile)));
            part.setFileName(attachedFile.getName());
            multipart.addBodyPart(part);
        }

        message.setContent(multipart);
        return message;
    }

    public void refresh() {
        threadPool.execute(UncheckedLambda.runnable(this::refresh0, Throwable::printStackTrace));
    }

    private void refresh0() throws MessagingException {
        Log.i("Fetching mails");

        Properties props = new Properties();
        String pop3Host = config.getStringThrowing("mail.pop3.host");
        props.put("mail.pop3.host", pop3Host);
        props.put("mail.pop3.port", config.getIntThrowing("mail.pop3.port"));
        String protocol = config.getString("mail.pop3.protocol").orElse("pop3");
        if(protocol.equalsIgnoreCase("pop3s")) {
            props.put("mail.pop3.ssl.enable", "true"); // TODO Config for this? See after testing at school
        }
        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore(protocol);
        store.connect(pop3Host,
            config.getString("mail.pop3.username").orElse(null),
            config.getString("mail.pop3.password").orElse(null));

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        Message[] messages = folder.getMessages();
        List<Mail> newMessages = Arrays.stream(messages)
            .map(UncheckedLambda.function(MailUtils::from))
            .collect(Collectors.toList());

        if((!newMessages.isEmpty())) {
            folder.setFlags(messages, DELETED_FLAG, true);

            // TODO Store mails in a local storage so they are not lost forever...
            Platform.runLater(() -> mailController.addMessages(newMessages));
        }

        folder.close(true);
        store.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Log.d("starting");
        config.load(getParameters().getNamed().get("config"));
        mailController = open("Mail.fxml", "InpresFPM - Mail", false, true);
        threadPool.scheduleAtFixedRate(
            UncheckedLambda.runnable(this::refresh, Throwable::printStackTrace),
            0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void stop() throws Exception {
        Log.d("Stopping");
        threadPool.shutdown();
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

}
