package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Client;
import ee.insa.crmapp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"api/client"})
public class ClientResource {
    private ClientService clientService;

    @Autowired
    public ClientResource(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/all")
    public List<Client> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/searchByName")
    public List<Client> findAllByNameContainsIgnoreCase(@RequestParam("name") String name) {
        return clientService.findAllByNameContainsIgnoreCase(name);
    }

    @PostMapping("/save")
    public Client save(@RequestBody Client client) {
        return clientService.save(client);
    }

    @GetMapping
    public Page<Client> getClients(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "100") int size) {
        return clientService.findAll(page, size);
    }

    @GetMapping("/search")
    public Page<Client> search(@RequestParam(name = "name", defaultValue = "") String name,
                               @RequestParam(name = "email", defaultValue = "") String email,
                               @RequestParam(name = "phone", defaultValue = "") String phone,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "100") int size) {
        return clientService.search(name, email, phone, page, size);
    }
}