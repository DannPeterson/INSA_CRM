package ee.insa.crmapp.service;

import ee.insa.crmapp.configuration.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private AppConfig appConfig;

    @Autowired
    public FileService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public List<String> getFolderFileList(String folderName) {
        List<String> fileNames = new ArrayList<>();
        String baseDirectory = appConfig.getBaseDirectory();
        File parentDirectory = new File(baseDirectory);
        File directory = new File(parentDirectory, folderName);

        try {
            // Check if the directory exists
            if (directory.exists() && directory.isDirectory()) {
                // Collect file names in the directory using Java NIO
                fileNames = Files.list(directory.toPath())
                        .filter(Files::isRegularFile)
                        .filter(path -> !path.toString().endsWith(".log"))  // Exclude .log files
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());
            } else {
                System.out.println("The specified directory does not exist or is not a directory.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    public void openDirectoryInExplorer(String folderName) {
        String baseDirectory = appConfig.getBaseDirectory();
        File parentDirectory = new File(baseDirectory);
        File directory = new File(parentDirectory, folderName);

        // Check if the directory exists; if not, create it
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
                return;
            }
        }

        try {
            // Open the directory in Windows Explorer
            Runtime.getRuntime().exec("explorer.exe " + directory.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error opening directory: " + e.getMessage());
        }
    }

    public void createDirectory(String folderName) {
        String baseDirectory = appConfig.getBaseDirectory();
        File parentDirectory = new File(baseDirectory);
        File directory = new File(parentDirectory, folderName);

        // Check if the directory exists; if not, create it
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }

    public void openFileInExplorer(File pdfFile) {
        // Check if the file exists
        if (!pdfFile.exists()) {
            System.err.println("File does not exist: " + pdfFile.getAbsolutePath());
            return;
        }

        try {
            // Open the PDF file directly using explorer.exe
            Runtime.getRuntime().exec("explorer.exe " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
        }
    }
}





