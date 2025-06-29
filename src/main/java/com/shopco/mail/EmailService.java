package com.shopco.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.pretty.MessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    @Async
    public void sendEmail(
            String to,
            String userName,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject

    ) throws MessagingException {
        String templeName;

        if (emailTemplate == null){
            templeName = "activate_account";
        }else{
            templeName = emailTemplate.name();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", userName);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation-code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("noreply@shopco.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templeName, context);
        helper.setText(template, true);

        mailSender.send(mimeMessage);

    }

}
