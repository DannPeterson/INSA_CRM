package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.SmsRequest;
import ee.insa.crmapp.service.LogService;
import ee.insa.crmapp.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/sms")
public class SmsResource {
    private final SmsService smsService;
    private final LogService logService;

    @Autowired
    public SmsResource(SmsService smsService,
                       LogService logService) {
        this.smsService = smsService;
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest) throws IOException {
        String report = smsService.sendSms(smsRequest);
        if(report.contains("\"status\":\"success\"")) {
            logService.addSmsLog(smsRequest);
            return ResponseEntity.ok(report);
        }
        return ResponseEntity.badRequest().body(report);
    }
}