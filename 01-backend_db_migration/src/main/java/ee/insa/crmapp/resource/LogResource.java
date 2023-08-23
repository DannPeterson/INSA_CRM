package ee.insa.crmapp.resource;

import ee.insa.crmapp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/logs")
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
}
