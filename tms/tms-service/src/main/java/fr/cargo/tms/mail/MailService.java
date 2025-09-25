package fr.cargo.tms.mail;

public interface MailService {
    void sendMovementXml(String subject, String to, String fileName, byte[] xmlBytes);
}

