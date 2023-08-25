package ee.insa.crmapp.resource;

import ee.insa.crmapp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/logs")
public class LogResource {
    private final LogService logService;

    @Autowired
    public LogResource(LogService logService) {
        this.logService = logService;
    }

    @RequestMapping("/{folder}")
    public List<String> getLogsByFolder(@PathVariable String folder) throws IOException {
        return logService.getLogs(folder);
    }

    @PostMapping("/banks")
    public List<String> getBankNameIfExists(@RequestBody List<String> folders) throws IOException {
        List<String> banks = new ArrayList<>();
        for (String folder : folders) {
            if(folder == null || folder.isEmpty()) {
                banks.add("");
            } else {
                List<String> logs = logService.getLogs(folder);
                banks.add(logService.getBankNameFromLogsIfExists(folder));
            }
        }
        return banks;
    }
}
