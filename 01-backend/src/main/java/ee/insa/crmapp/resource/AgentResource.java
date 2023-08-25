package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Agent;
import ee.insa.crmapp.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"api/agent"})
public class AgentResource {
    private final AgentService agentService;

    @Autowired
    public AgentResource(AgentService agentService){
        this.agentService = agentService;
    }

    @GetMapping("/all")
    public List<Agent> findAll(){
        return agentService.findAll();
    }
}