package fr.cargo.tms.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class SmtpMailService implements MailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@local.test}")
    private String from;

    @Override
    public void sendMovementXml(String subject, String to, String fileName, byte[] xmlBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("Mouvement créé. Voir la pièce jointe XML.", false);
            helper.addAttachment(fileName, new ByteArrayResource(xmlBytes), "application/xml");
            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("SMTP send failed", e);
        }
    }
}

