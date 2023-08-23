package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p ORDER BY p.conclusionDate DESC")
    Page<Policy> findLastPolicies(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Policy p")
    long countPolicies();

    Page<Policy> findByOrderByIdDesc(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Policy p " +
            "JOIN p.policyType pt " +
            "LEFT JOIN p.paymentType pymt " +
            "LEFT JOIN p.client c " +
            "LEFT JOIN p.agent a " +
            "LEFT JOIN PolicyPart pp ON p.id = pp.policy.id " +
            "LEFT JOIN Invoice i ON pp.invoice.id = i.id " +
            "WHERE (COALESCE(:policyTypeId, 0) = 0 OR pt.id = :policyTypeId) " +
            "AND (COALESCE(:paymentTypeId, 0) = 0 OR pymt.id = :paymentTypeId OR pymt.id IS NULL) " +
            "AND (COALESCE(:clientName, '') = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :clientName, '%')) OR c IS NULL) " +
            "AND (COALESCE(:object, '') = '' OR LOWER(p.object) LIKE LOWER(CONCAT('%', :object, '%'))) " +
            "AND (COALESCE(:invoiceNumber, '') = '' OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :invoiceNumber, '%'))) " +
            "AND (COALESCE(:policyNumber, '') = '' OR LOWER(p.policyNumber) LIKE LOWER(CONCAT('%', :policyNumber, '%'))) " +
            "AND (:current = false OR p.endDate >= CURRENT_DATE) " +
            "ORDER BY p.id DESC")
    Page<Policy> findPoliciesBySearchCriteria(
            @Param("policyTypeId") Long policyTypeId,
            @Param("paymentTypeId") Long paymentTypeId,
            @Param("clientName") String clientName,
            @Param("object") String object,
            @Param("invoiceNumber") String invoiceNumber,
            @Param("policyNumber") String policyNumber,
            @Param("current") boolean current,
            Pageable pageable
    );



    @Query("SELECT DISTINCT p FROM Policy p " +
            "JOIN p.policyType pt " +
            "WHERE (COALESCE(:policyTypeId, 0) = 0 OR pt.id = :policyTypeId) " +
            "AND p.stopDate IS NULL " +
            "AND p.reminder = TRUE " +
            "AND p.reminderDate <= :today " +
            "ORDER BY p.reminderDate ASC")
    Page<Policy> searchReminderPolicies(@Param("policyTypeId") Long policyTypeId,
                                        @Param("today") LocalDate today,
                                        Pageable pageable);


    @Modifying
    @Query(value = "INSERT INTO policy (id, client_id, insurer_id, policy_type_id, payment_type_id, agent_id, user_id, conclusion_date, start_date, end_date, stop_date, reminder_date, reminder, object, folder, policy_number, sum, percent, provision, comment, parts) VALUES (:id, :clientId, :insurerId, :policyTypeId, :paymentTypeId, :agentId, :userId, :conclusionDate, :startDate, :endDate, :stopDate, :reminderDate, :reminder, :object, :folder, :policyNumber, :sum, :percent, :provision, :comment, :parts)", nativeQuery = true)
    void insertPolicyWithId(
            @Param("id") Long id,
            @Param("clientId") Long clientId,
            @Param("insurerId") Long insurerId,
            @Param("policyTypeId") Long policyTypeId,
            @Param("paymentTypeId") Long paymentTypeId,
            @Param("agentId") Long agentId,
            @Param("userId") Long userId,
            @Param("conclusionDate") LocalDate conclusionDate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stopDate") LocalDate stopDate,
            @Param("reminderDate") LocalDate reminderDate,
            @Param("reminder") Boolean reminder,
            @Param("object") String object,
            @Param("folder") String folder,
            @Param("policyNumber") String policyNumber,
            @Param("sum") Double sum,
            @Param("percent") BigDecimal percent,
            @Param("provision") Double provision,
            @Param("comment") String comment,
            @Param("parts") Integer parts
    );

    @Modifying
    @Query("UPDATE Policy p SET p.client.id = :newClientId WHERE p.client.id = :oldClientId")
    void updateClient(@Param("oldClientId") Long oldClientId, @Param("newClientId") Long newClientId);
}
