package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Client;
import ee.insa.crmapp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public List<Client> findAll(){
        return clientRepository.findAll();
    }

    public Page<Client> findAll(int pageIndex, int pageSize) {
        return clientRepository.findAll(PageRequest.of(pageIndex, pageSize));
    }

    public Page<Client> search(String name, String email, String phone, int pageIndex, int pageSize) {
        return clientRepository.search(name, email, phone, PageRequest.of(pageIndex, pageSize));
    }

    public List<Client> findAllByNameContainsIgnoreCase(String name){
        return clientRepository.findAllByNameContainsIgnoreCase(name);
    }

    public Client findByCode(String code){
        return clientRepository.findByCode(code);
    }

    @Transactional
    public void insertClientWithId(Client client) {
        clientRepository.insertClientWithId(
                client.getId(),
                client.getName(),
                client.getAddress(),
                client.getCode(),
                client.getPhone(),
                client.getMobile1(),
                client.getMobile2(),
                client.getEmail1(),
                client.getEmail2(),
                client.getEmail3(),
                client.getRepresentative(),
                client.getBank() != null ? client.getBank().getId() : null,
                client.getBankAccount(),
                client.getComment()
        );
    }

    List<Client> findAllByCode(String code) {
        return clientRepository.findAllByCode(code);
    }

    boolean existsByCode(String code){
        return clientRepository.existsByCode(code);
    }

    List<String> findDuplicateCodes(){
        return clientRepository.findDuplicateCodes();
    }

    void deleteById(Long id){
        clientRepository.deleteById(id);
    }
}