package fr.cargo.tms.mail;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.cargo.tms.contracts.model.MovementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MovementXmlBuilder {

    private final XmlMapper xmlMapper = new XmlMapper();

    public byte[] buildXml(MovementDto movement) {
        // En-tête minimal conforme aux exemples du sujet (from/to/date/id)
        var envelope = new MovementEnvelope()
                .setHeader(new MovementHeader()
                        .setFrom("RAPIDCARGO")
                        .setTo("CARGOINFO")
                        .setMessageTime(OffsetDateTime.now().toString())
                        .setMessageId(UUID.randomUUID().toString()))
                .setMovement(movement);
        try {
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(envelope);
        } catch (Exception e) {
            throw new IllegalStateException("XML generation failed", e);
        }
    }

    // --- DTOs XML (simples, adaptés au sujet)
    public static class MovementEnvelope {
        private MovementHeader header;
        private MovementDto movement;
        public MovementHeader getHeader() { return header; }
        public MovementEnvelope setHeader(MovementHeader h) { this.header = h; return this; }
        public MovementDto getMovement() { return movement; }
        public MovementEnvelope setMovement(MovementDto m) { this.movement = m; return this; }
    }
    public static class MovementHeader {
        private String from, to, messageTime, messageId;
        public String getFrom() { return from; }
        public MovementHeader setFrom(String v) { this.from = v; return this; }
        public String getTo() { return to; }
        public MovementHeader setTo(String v) { this.to = v; return this; }
        public String getMessageTime() { return messageTime; }
        public MovementHeader setMessageTime(String v) { this.messageTime = v; return this; }
        public String getMessageId() { return messageId; }
        public MovementHeader setMessageId(String v) { this.messageId = v; return this; }
    }
}

