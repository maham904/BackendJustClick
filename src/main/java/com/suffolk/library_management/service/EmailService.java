package com.suffolk.library_management.service;


import com.suffolk.library_management.model.SendEmailModel;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.suffolk.library_management.utils.Constant.STATUS_CODE_ZERO;


@Service
@Data
@RequiredArgsConstructor
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JavaMailSender mailSender;

    @Value("${base_url}")
    private String baseUrl;

//    @Value("${mail.host}")
//    private String host;
//
//    @Value("${mail.port}")
//    private int port;
//
//    @Value("${mail.username}")
//    private String userName;
//
//    @Value("${mail.password}")
//    private String password;
//
//    @Value("${mail.from}")
//    private String from;

    private final TemplateEngine templateEngine;

    private final ResourceLoader resourceLoader;

    private int status = STATUS_CODE_ZERO;
    private String message = "";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Register User And Forget Password
     **/
    public void sendVerificationEmail(SendEmailModel emailModel, boolean verification) {

        scheduler.schedule(() -> {
            MimeMessage mail = mailSender.createMimeMessage();
            // Prepare the evaluation context
            Context context = new Context();
            context.setVariable("verificationModel", emailModel);
            String htmlContent;
            if (verification) { // Register User Verification Link
                htmlContent = templateEngine.process("account_verification_email_template", context);
            } else {
                htmlContent = templateEngine.process("forget_password_email_template", context);
            }
            try {
//                mailSender.setHost(host);
//                mailSender.setPort(port);
//                mailSender.setUsername(userName);
//                mailSender.setPassword(password);
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(emailModel.getUserEmail());
//                helper.setReplyTo(from);
//                helper.setFrom(from, APP_NAME);
                helper.setSubject(emailModel.getSubject());
                mail.setContent(htmlContent, "text/html");
                mailSender.send(mail);
                logger.info("Email Send SuccessFully");
            } catch (Exception e) {
                String message = String.format("Error sending email due to %s", e.getMessage());
                logger.error(message);
            }

        }, 1, TimeUnit.SECONDS);
    }

}
