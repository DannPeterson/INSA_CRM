package ee.insa.crmapp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${base_directory}")
    private String baseDirectory;
    @Value("${insa.email.default-text}")
    private String emailDefaultText;
    @Value("${insa.sms.api-key}")
    private String smsApiKey;
    @Value("${insa.sms.sender}")
    private String smsSender;
    @Value("${insa.sms.text-rus}")
    private String smsTextRus;
    @Value("${insa.sms.text-est}")
    private String smsTextEst;
    @Value("${google.request.amount}")
    private String googleRequestAmount;

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public String getEmailDefaultText() {
        return emailDefaultText;
    }

    public String getSmsApiKey() {
        return smsApiKey;
    }

    public String getSmsSender() {
        return smsSender;
    }

    public String getSmsTextRus() {
        return smsTextRus;
    }

    public String getSmsTextEst() {
        return smsTextEst;
    }

    public String getGoogleRequestAmount() {
        return googleRequestAmount;
    }
}
