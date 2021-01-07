package com.iyfbackend.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.file.Paths;

@Service
@Slf4j
public class EmailService {

    private final TemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendMailWithAttachment(String to, String subject, String templateName, String fileToAttach, Context context) throws MessagingException {

        String process = templateEngine.process(templateName, context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(subject);
        helper.setText(process, true);
        helper.setTo(to);
        FileSystemResource file = new FileSystemResource(Paths.get("src", "main", "resources", fileToAttach).toFile());
        helper.addAttachment("Umang Brochure.pdf", file);
        javaMailSender.send(mimeMessage);
        log.info("Email sent to user :: {} ", to);
    }

    public String sendMail(String to, String subject, String templateName, Context context) throws MessagingException {

        String process = templateEngine.process(templateName, context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(subject);
        helper.setText(process, true);
        helper.setTo(to);
        javaMailSender.send(mimeMessage);
        return "Sent";
    }
}
