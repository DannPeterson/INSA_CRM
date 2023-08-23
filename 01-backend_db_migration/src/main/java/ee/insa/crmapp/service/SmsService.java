package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Policy;
import ee.insa.crmapp.model.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SmsService {
    @Value("${insa.sms.api-key}")
    private String smsApiKey;
    private final static String SMS_URL = "https://api.txtlocal.com/send/?";
    private final PolicyService policyService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public SmsService(PolicyService policyService) {
        this.policyService = policyService;
    }

    public String sendSms(SmsRequest smsRequest) {
        String toNorm = smsRequest.getTo().replaceAll("\\D", "");
        if (!toNorm.startsWith("372")) {
            toNorm = "372" + toNorm;
        }

        String fromNorm = smsRequest.getFrom().replaceAll("\\D", "");
        if (!fromNorm.startsWith("372")) {
            fromNorm = "372" + fromNorm;
        }

        String apiKey = "apikey=" + smsApiKey;
        String message = "&message=" + smsRequest.getMessage();
        String sender = "&sender=" + fromNorm;
        String numbers = "&numbers=" + toNorm;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(SMS_URL).openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            bufferedReader.close();
            addSmsComment(smsRequest);
            return stringBuffer.toString();
        } catch (Exception e) {
            return "Error sending SMS";
        }
    }

    private void addSmsComment(SmsRequest smsRequest) {
        Policy policy = policyService.findById(smsRequest.getPolicyId());
        if (policy.getComment() != null && !policy.getComment().isEmpty()) {
            policy.setComment(policy.getComment() + "; SMS " + LocalDate.now().format(formatter));
        } else {
            policy.setComment("SMS " + LocalDate.now().format(formatter));
        }
        policyService.save(policy);
    }
}
