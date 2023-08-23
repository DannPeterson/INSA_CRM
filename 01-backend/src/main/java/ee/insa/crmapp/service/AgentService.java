package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Agent;
import ee.insa.crmapp.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentService {
    private final AgentRepository agentRepository;

    @Autowired
    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent save(Agent agent) {
        return agentRepository.save(agent);
    }

    public Agent findById(Long id) {
        return agentRepository.findById(id).orElse(null);
    }

    public List<Agent> findAll(){
        return agentRepository.findAll();
    }

    public Agent findByPrefix(String prefix) {
        return agentRepository.findByPrefix(prefix);
    }
}
