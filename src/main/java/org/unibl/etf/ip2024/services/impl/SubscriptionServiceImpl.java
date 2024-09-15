package org.unibl.etf.ip2024.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.entities.CategoryEntity;
import org.unibl.etf.ip2024.models.entities.FitnessProgramEntity;
import org.unibl.etf.ip2024.models.entities.SubscriptionEntity;
import org.unibl.etf.ip2024.repositories.CategoryEntityRepository;
import org.unibl.etf.ip2024.repositories.FitnessProgramEntityRepository;
import org.unibl.etf.ip2024.repositories.SubscriptionEntityRepository;
import org.unibl.etf.ip2024.services.EmailService;
import org.unibl.etf.ip2024.services.LogService;
import org.unibl.etf.ip2024.services.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final SubscriptionEntityRepository subscriptionRepository;
    private final EmailService emailService;
    private final CategoryEntityRepository categoryRepository;
    private final LogService logService;


    @Scheduled(cron = "0 0 6 * * ?")
    public void sendDailySubscriptionEmails() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        List<CategoryEntity> categories = categoryRepository.findAll();
        for (CategoryEntity category : categories) {
            List<FitnessProgramEntity> newPrograms = fitnessProgramRepository.findAllByCategoryAndCreatedAtAfter(category, yesterday);
            if (!newPrograms.isEmpty()) {
                List<SubscriptionEntity> subscriptions = subscriptionRepository.findAllByCategory(category);
                for (SubscriptionEntity subscription : subscriptions) {
                    String emailContent = createEmailContent(newPrograms);
                    emailService.sendEmail(subscription.getUser().getEmail(), "Novi programi za kategoriju: " + category.getName(), emailContent);
                }
            }
        }
        logService.log(null, "Slanje emailova o novim programima");

    }

    private String createEmailContent(List<FitnessProgramEntity> newPrograms) {
        StringBuilder content = new StringBuilder();
        content.append("<div style=\"font-family: Arial, sans-serif; font-size: 16px;\">");
        content.append("<h2>Novi programi kreirani u poslednja 24 sata</h2>");

        for (FitnessProgramEntity program : newPrograms) {
            content.append("<div style=\"margin-bottom: 20px; padding: 10px; border-bottom: 1px solid #ccc;\">")
                    .append("<h3 style=\"color: #333;\">Naziv: ").append(program.getName()).append("</h3>")
                    .append("<p><strong>Opis:</strong> ").append(program.getDescription()).append("</p>")
                    .append("<p><strong>Cijena:</strong> ").append(program.getPrice()).append(" €</p>")
                    .append("<p><strong>Težina:</strong> ").append(program.getDifficultyLevel()).append("</p>")
                    .append("</div>");
        }

        content.append("<p>Hvala što koristite našu platformu!</p>");
        content.append("</div>");

        return content.toString();
    }

}
