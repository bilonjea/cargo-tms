package fr.cargo.tms.mail;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class MailjetMailService implements MailService {

    private final MailjetClient client;

    @Value("${app.mail.from}")
    private String from;
    @Value("${app.mail.fromName:RapidCargo}")
    private String fromName;

    @Override
    public void sendMovementXml(String subject, String to, String fileName, byte[] xmlBytes) {
        try {
            String b64 = Base64.getEncoder().encodeToString(xmlBytes);
            JSONObject message = new JSONObject()
                    .put(Emailv31.Message.FROM, new JSONObject()
                            .put("Email", from)
                            .put("Name", fromName))
                    .put(Emailv31.Message.TO, new JSONArray().put(new JSONObject().put("Email", to)))
                    .put(Emailv31.Message.SUBJECT, subject)
                    .put(Emailv31.Message.TEXTPART, "Mouvement créé. PJ XML incluse.")
                    .put(Emailv31.Message.ATTACHMENTS, new JSONArray().put(
                            new JSONObject()
                                    .put("ContentType", "application/xml")
                                    .put("Filename", fileName)
                                    .put("Base64Content", b64)
                    ));
            MailjetRequest req = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, new JSONArray().put(message));
            client.post(req);
        } catch (Exception e) {
            throw new IllegalStateException("Mailjet send failed", e);
        }
    }
}
