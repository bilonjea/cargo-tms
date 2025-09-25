package fr.cargo.tms.events;

import fr.cargo.tms.mail.MailService;
import fr.cargo.tms.mail.MovementXmlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class MovementEmailListener {
    private final MovementXmlBuilder xmlBuilder;
    private final MailService mailService;

    @Value("${app.mail.enabled:true}")         private boolean mailEnabled;
    @Value("${app.mail.to:dev@local.test}")    private String mailTo;
    @Value("${app.mail.subject:Movement #{id}}") private String subjectTpl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(MovementCreatedEvent ev) {
        if (!mailEnabled) return;
        byte[] xml = xmlBuilder.buildXml(ev.movement());
        String subject = subjectTpl.replace("#{id}", String.valueOf(ev.movementId()));
        String fileName = "movement-" + ev.movementId() + ".xml";
        mailService.sendMovementXml(subject, mailTo, fileName, xml);
    }
}

