package ee.insa.crmapp.service;

import ee.insa.crmapp.configuration.AppConfig;
import ee.insa.crmapp.model.EmailRequest;
import ee.insa.crmapp.model.LogEntry;
import ee.insa.crmapp.model.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {
    private AppConfig appConfig;
    private final PolicyService policyService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    public LogService(AppConfig appConfig, PolicyService policyService) {
        this.appConfig = appConfig;
        this.policyService = policyService;
    }

    public void addEmailLog(EmailRequest emailRequest) throws IOException {
        String attachments = String.join(", ", emailRequest.getAttachments());
        String recipients = String.join(", ", emailRequest.getTo());
        String message = String.format("Email >>> %s, файлы: %s", recipients, attachments);
        LogEntry logEntry = new LogEntry(message, LocalDateTime.now());
        addLog(emailRequest.getFolder(), logEntry);
    }

    public void addSmsLog(SmsRequest smsRequest) throws IOException {
        String recipients = String.join(", ", smsRequest.getTo());
        String message = String.format("SMS >>> %s", recipients);
        LogEntry logEntry = new LogEntry(message, LocalDateTime.now());
        addLog(smsRequest.getFolder(), logEntry);
    }

    private void addLog(String folder, LogEntry logEntry) throws IOException {
        Path logPath = getLogPath(folder);
        String logMessage = logEntry.getCreationTime().format(formatter) + " " + logEntry.getMessage();
        Files.write(logPath, (logMessage + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public List<String> getLogs(String folder) throws IOException {
        List<String> logs = new ArrayList<>();

        Path legacyLogPath = getLegacyLogPath(folder);
        Path logPath = getLogPath(folder);

        if(Files.exists(legacyLogPath)) {
            Charset charset = Charset.forName("Windows-1252");
            logs.addAll(Files.readAllLines(legacyLogPath, charset));
        }
        if(Files.exists(logPath)) {
            logs.addAll(Files.readAllLines(logPath));
        }
        return logs;
    }

    private Path getLogPath(String folder) throws IOException {
        Path path = Paths.get(appConfig.getBaseDirectory() + "\\" + folder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path.resolve(".log");
    }

    private Path getLegacyLogPath(String folder) throws IOException {
        Path path = Paths.get(appConfig.getBaseDirectory() + "\\" + folder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path.resolve("log.txt");
    }
}
