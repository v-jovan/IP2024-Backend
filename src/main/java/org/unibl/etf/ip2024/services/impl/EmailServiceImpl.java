package org.unibl.etf.ip2024.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.exceptions.EmailSendException;
import org.unibl.etf.ip2024.services.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendActivationEmail(String to, String activationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Aktivacija naloga");
            helper.setText(buildEmail(activationLink), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("Greska prilikom slanja emaila", e);
        }
    }

    private String buildEmail(String activationLink) {
        // This could be a template, but just for this example I will hardcode it
        return "<div style=\"font-family: Arial, sans-serif; font-size: 16px;\">" +
                "<h2>Aktivacija naloga</h2>" +
                "<p>Hvala što ste se registrovali. Kliknite na link ispod kako biste aktivirali vaš nalog:</p>" +
                "<a href=\"" + activationLink + "\">Aktivirajte nalog</a>" +
                "<p>Link je validan 24 sata.</p>" +
                "<p>Hvala,</p>" +
                "<p>Vaš tim</p>" +
                "</div>";
    }
}
