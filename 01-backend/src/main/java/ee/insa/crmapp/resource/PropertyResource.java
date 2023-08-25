package ee.insa.crmapp.resource;

import ee.insa.crmapp.configuration.AppConfig;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/property")
public class PropertyResource {
    private final AppConfig appConfig;

    @Autowired
    public PropertyResource(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("insa.email.default-text", appConfig.getEmailDefaultText());
        properties.put("insa.sms.api-key", appConfig.getSmsApiKey());
        properties.put("insa.sms.sender", appConfig.getSmsSender());
        properties.put("insa.sms.text-rus", appConfig.getSmsTextRus());
        properties.put("insa.sms.text-est", appConfig.getSmsTextEst());
        properties.put("google.request.amount", appConfig.getGoogleRequestAmount());

        return ResponseEntity.ok(properties);
    }
}
