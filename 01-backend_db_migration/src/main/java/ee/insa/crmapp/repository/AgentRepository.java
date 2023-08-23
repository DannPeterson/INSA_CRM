package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Agent findByPrefix(String prefix);
}
