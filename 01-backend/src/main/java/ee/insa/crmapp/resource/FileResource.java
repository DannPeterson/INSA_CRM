package ee.insa.crmapp.resource;

import ee.insa.crmapp.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"api/files"})
public class FileResource {
    private final FileService fileService;

    @Autowired
    public FileResource(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{folderName}")
    public List<String> getFolderFiles(@PathVariable("folderName") String folderName) {
        System.out.println("IN getFolderFiles");
        return fileService.getFolderFileList(folderName);
    }

    @GetMapping("/open/{folderName}")
    public void openFolderInExplorer(@PathVariable("folderName") String folderName) {
        fileService.openDirectoryInExplorer(folderName);
    }
}
