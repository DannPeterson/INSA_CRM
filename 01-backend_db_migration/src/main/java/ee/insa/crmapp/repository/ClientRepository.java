package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByNameContainsIgnoreCase(String name);

    @Override
    Page<Client> findAll(Pageable pageable);

    @Query("SELECT c FROM Client c WHERE " +
            "(COALESCE(:name, '') = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(COALESCE(:email, '') = '' OR LOWER(c.email1) LIKE LOWER(CONCAT('%', :email, '%')) OR LOWER(c.email2) LIKE LOWER(CONCAT('%', :email, '%')) OR LOWER(c.email3) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(COALESCE(:phone, '') = '' OR REPLACE(c.phone, ' ', '') LIKE CONCAT('%', REPLACE(:phone, ' ', ''), '%') OR REPLACE(c.mobile1, ' ', '') LIKE CONCAT('%', REPLACE(:phone, ' ', ''), '%') OR REPLACE(c.mobile2, ' ', '') LIKE CONCAT('%', REPLACE(:phone, ' ', ''), '%'))")
    Page<Client> search(@Param("name") String name, @Param("email") String email, @Param("phone") String phone, Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO client (id, name, address, code, phone, mobile1, mobile2, email1, email2, email3, representative, bank_id, bank_account, comment) VALUES (:id, :name, :address, :code, :phone, :mobile1, :mobile2, :email1, :email2, :email3, :representative, :bank_id, :bank_account, :comment)", nativeQuery = true)
    void insertClientWithId(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("address") String address,
            @Param("code") String code,
            @Param("phone") String phone,
            @Param("mobile1") String mobile1,
            @Param("mobile2") String mobile2,
            @Param("email1") String email1,
            @Param("email2") String email2,
            @Param("email3") String email3,
            @Param("representative") String representative,
            @Param("bank_id") Long bank_id,
            @Param("bank_account") String bank_account,
            @Param("comment") String comment
    );

    boolean existsByCode(String code);

    Client findByCode(String code);

    List<Client> findAllByCode(String code);

    @Query("SELECT c.code FROM Client c GROUP BY c.code HAVING COUNT(c) > 1")
    List<String> findDuplicateCodes();
}
