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
import org.unibl.etf.ip2024.services.LogService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final LogService logService;

    @Override
    @Async
    public void sendActivationEmail(String to, String activationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Aktivacija naloga");
            helper.setText(buildActivationEmail(activationLink), true);

            logService.log(null, "Slanje emaila za aktivaciju naloga");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("Greska prilikom slanja emaila", e);
        }
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            logService.log(null, "Slanje emaila");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("Greška prilikom slanja emaila", e);
        }
    }

    private String buildActivationEmail(String activationLink) {
        // This could be a template, but just for this example I will hardcode it
        return "<div style=\"font-family: Arial, sans-serif; font-size: 16px;\">" +
                "<h2>Aktivacija naloga</h2>" +
                "<p>Hvala što ste se registrovali. Kliknite na link ispod kako biste aktivirali vaš nalog:</p>" +
                "<a href=\"" + activationLink + "\">Aktivirajte nalog</a>" +
                "<p>Drago nam je što se družimo.</p>" +
                "<p>Hvala,</p>" +
                "<p>Vaš tim</p>" +
                "</div>";
    }
}
