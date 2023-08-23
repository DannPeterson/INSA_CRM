package ee.insa.crmapp.resource;

import ee.insa.crmapp.google.SheetsService;
import ee.insa.crmapp.model.PolicyPart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = {"/google"})
public class GoogleResource {
    private final SheetsService sheetsService;

    public GoogleResource(SheetsService sheetsService) {
        this.sheetsService = sheetsService;
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkPart(@RequestBody PolicyPart policyPart) throws IOException {
        Map<String, String> response = new HashMap<>();
        if(sheetsService.isPartInserted(policyPart)) {
            response.put("status", "inserted");
        } else {
            response.put("status", "missing");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<String, String>> insertPart(@RequestBody PolicyPart policyPart) throws IOException {
        Map<String, String> response = new HashMap<>();
        if(sheetsService.insertPolicyPart(policyPart)) {
            response.put("status", "inserted");
        } else {
            response.put("status", "not inserted");
        }
        return ResponseEntity.ok(response);
    }
}
