package com.nouraschool.notifications;

import com.nouraschool.domain.services.EmailService;
import com.nouraschool.domain.services.impl.EmailServiceImpl;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests EmailService — Mock SMTP (task.md FOLDER 11).
 */
@QuarkusTest
class EmailServiceTest {

    @Inject
    EmailService emailService;

    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void setUp() {
        mailbox.clear();
    }

    @Test
    @DisplayName("send envoie un email avec sujet et corps")
    void send_addsMailToMailbox() {
        emailService.send("dest@test.com", "Test", "Contenu du mail");

        List<Mail> sent = mailbox.getMessagesSentTo("dest@test.com");
        assertThat(sent).hasSize(1);
        assertThat(sent.get(0).getSubject()).isEqualTo("Test");
        assertThat(sent.get(0).getText()).contains("Contenu du mail");
    }

    @Test
    @DisplayName("sendCompteCree envoie les identifiants")
    void sendCompteCree_sendsCredentials() {
        emailService.sendCompteCree("nouveau@test.com", "Dupont", "Jean", "nouveau@test.com", "Temp123!");

        List<Mail> sent = mailbox.getMessagesSentTo("nouveau@test.com");
        assertThat(sent).hasSize(1);
        assertThat(sent.get(0).getText())
                .contains("Jean", "Dupont", "nouveau@test.com", "Temp123!");
    }

    @Test
    @DisplayName("sendResetMdp envoie le code de réinitialisation")
    void sendResetMdp_sendsCode() {
        emailService.sendResetMdp("user@test.com", "987654", "60");

        List<Mail> sent = mailbox.getMessagesSentTo("user@test.com");
        assertThat(sent).hasSize(1);
        assertThat(sent.get(0).getText()).contains("987654", "60");
    }
}
