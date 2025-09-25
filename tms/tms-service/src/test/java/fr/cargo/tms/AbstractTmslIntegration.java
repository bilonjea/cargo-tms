package fr.cargo.tms;

import fr.cargo.tms.events.MovementEmailListener;
import fr.cargo.tms.mail.MailService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TmsServiceApplication.class)
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext
public class AbstractTmslIntegration {

    @Autowired
    protected EntityManager entityManager;

    @MockitoBean
    MailService mailService;

    @MockitoBean
    MovementEmailListener movementEmailListener;
}
