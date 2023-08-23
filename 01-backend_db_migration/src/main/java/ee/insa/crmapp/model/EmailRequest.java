package ee.insa.crmapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailRequest {
    private String from;
    private List<String> to;
    private String subject;
    private String body;
    private String folder;
    private List<String> attachments;
    private boolean sendMeCopy;
}