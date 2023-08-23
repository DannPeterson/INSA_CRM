package ee.insa.crmapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SmsRequest {
    private Long policyId;
    private String folder;
    private String from;
    private String to;
    private String message;
}
