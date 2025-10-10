import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class SimpleEmail {
    public static void main(String[] args) {
        String from = "teuemail@gmail.com";
        String to = "destinatario@gmail.com";
        String password = "tua-app-password"; // não é a password normal, é uma "app password"

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Teste com SMTP");
            message.setText("Olá! Este email foi enviado sem API key, apenas via SMTP.");

            Transport.send(message);
            System.out.println("Email enviado com sucesso!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
