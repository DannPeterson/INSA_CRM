package ee.insa.crmapp.service;

import ee.insa.crmapp.configuration.AppConfig;
import ee.insa.crmapp.model.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class MailService {

    private final AppConfig appConfig;
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    String from;

    @Autowired
    public MailService(JavaMailSender emailSender,
                       AppConfig appConfig) {
        this.emailSender = emailSender;
        this.appConfig = appConfig;
    }

    public void sendEmailWithAttachments(EmailRequest emailRequest) throws MessagingException {
        try {
            String fullPath = appConfig.getBaseDirectory() + "\\" + emailRequest.getFolder() + "\\";
            List<File> files = new ArrayList<>();
            for(String file : emailRequest.getAttachments()) {
                files.add(new File(fullPath + file));
            }

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            System.out.println("To: " + emailRequest.getTo());
            helper.setTo(emailRequest.getTo().toArray(new String[0]));
            if(emailRequest.isSendMeCopy()) helper.setCc(from);
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), false);

            if (files != null) {
                for (File file : files) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email with attachments", e);
        }
    }
}