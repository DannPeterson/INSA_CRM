package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.EmailRequest;
import ee.insa.crmapp.service.LogService;
import ee.insa.crmapp.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/mail")
public class MailResource {
    private final MailService mailService;
    private final LogService logService;

    @Autowired
    public MailResource(MailService mailService,
                        LogService logService) {
        this.mailService = mailService;
        this.logService = logService;
    }

    @PostMapping()
    public ResponseEntity<String> sendEmailWithAttachments(@RequestBody EmailRequest emailRequest) throws IOException, MessagingException {
        mailService.sendEmailWithAttachments(emailRequest);
        logService.addEmailLog(emailRequest);
        return ResponseEntity.ok("{\"message\":\"Email sent successfully\"}");
    }
}