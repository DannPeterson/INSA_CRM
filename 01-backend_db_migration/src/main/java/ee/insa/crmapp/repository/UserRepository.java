package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
