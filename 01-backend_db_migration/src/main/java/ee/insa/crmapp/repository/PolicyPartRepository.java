package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PolicyPartRepository extends JpaRepository<PolicyPart, Long> {
    List<PolicyPart> findAllByPolicy(Policy policy);

    @Modifying
    @Query(value = "INSERT INTO policy_part (id, policy_id, part, sum, date, date_paid, date_confirmed, sum_real, reminder) VALUES (:id, :policyId, :part, :sum, :date, :datePaid, :dateConfirmed, :sumReal, :reminder)", nativeQuery = true)
    void insertPolicyPartWithId(
            @Param("id") Long id,
            @Param("policyId") Long policyId,
            @Param("part") Integer part,
            @Param("sum") Double sum,
            @Param("date") LocalDate date,
            @Param("datePaid") LocalDate datePaid,
            @Param("dateConfirmed") LocalDate dateConfirmed,
            @Param("sumReal") Double sumReal,
            @Param("reminder") boolean reminder
    );

    PolicyPart findByInvoice(Invoice invoice);

    Page<PolicyPart> findByReminderTrueAndDatePaidIsNullOrderByDateAsc(Pageable pageable);

    @Query("SELECT pp FROM PolicyPart pp WHERE pp.part > pp.policy.parts")
    List<PolicyPart> findExcessParts();

    @Query("SELECT pp FROM PolicyPart pp WHERE pp.policy.id = :policyId AND pp.part > pp.policy.parts")
    List<PolicyPart> findExcessPartsForPolicy(Long policyId);

    void deleteInBatch(Iterable<PolicyPart> policyParts);

    //AGENT REPORT
    @Query("SELECT pp FROM PolicyPart pp WHERE pp.policy.agent.id = :agentId AND pp.policy.stopDate IS NULL AND pp.date BETWEEN :start AND :end")
    List<PolicyPart> findPolicyPartsByAgentAndDateBetween(@Param("agentId") long agentId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp WHERE pp.policy.agent.id = :agentId AND pp.policy.stopDate IS NULL AND pp.datePaid BETWEEN :start AND :end")
    List<PolicyPart> findPolicyPartsByAgentAndDatePaidBetween(@Param("agentId") long agentId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp WHERE pp.policy.agent.id = :agentId AND pp.policy.stopDate IS NULL AND pp.date BETWEEN :start AND :end AND pp.datePaid IS NULL")
    List<PolicyPart> findPolicyPartsByAgentAndDateBetweenAndDatePaidIsNull(@Param("agentId") long agentId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    //INSURER REPORT
    @Query("SELECT pp FROM PolicyPart pp " +
            "JOIN pp.policy p " +
            "WHERE (p.insurer.id = :insurerId) " +
            "AND (p.stopDate IS NULL) " +
            "AND (:policyTypeId IS NULL OR p.policyType.id = :policyTypeId) " +
            "AND (:paymentTypeId IS NULL OR p.paymentType.id = :paymentTypeId) " +
            "AND (pp.date BETWEEN :start AND :end)")
    List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_All(Long insurerId,
                                                                              Long policyTypeId,
                                                                              Long paymentTypeId,
                                                                              LocalDate start,
                                                                              LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp " +
            "JOIN pp.policy p " +
            "WHERE (p.insurer.id = :insurerId) " +
            "AND (p.stopDate IS NULL) " +
            "AND (:policyTypeId IS NULL OR p.policyType.id = :policyTypeId) " +
            "AND (:paymentTypeId IS NULL OR p.paymentType.id = :paymentTypeId) " +
            "AND (pp.date BETWEEN :start AND :end)" +
            "AND pp.datePaid IS NOT NULL")
    List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Paid(Long insurerId,
                                                                               Long policyTypeId,
                                                                               Long paymentTypeId,
                                                                               LocalDate start,
                                                                               LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp " +
            "JOIN pp.policy p " +
            "WHERE (p.insurer.id = :insurerId) " +
            "AND (p.stopDate IS NULL) " +
            "AND (:policyTypeId IS NULL OR p.policyType.id = :policyTypeId) " +
            "AND (:paymentTypeId IS NULL OR p.paymentType.id = :paymentTypeId) " +
            "AND (pp.date BETWEEN :start AND :end)" +
            "AND pp.datePaid IS NULL")
    List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Debt(Long insurerId,
                                                                               Long policyTypeId,
                                                                               Long paymentTypeId,
                                                                               LocalDate start,
                                                                               LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp " +
            "JOIN pp.policy p " +
            "WHERE (p.insurer.id = :insurerId) " +
            "AND (p.stopDate IS NULL) " +
            "AND (:policyTypeId IS NULL OR p.policyType.id = :policyTypeId) " +
            "AND (:paymentTypeId IS NULL OR p.paymentType.id = :paymentTypeId) " +
            "AND (p.conclusionDate BETWEEN :start AND :end)")
    List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_All(Long insurerId,
                                                                                     Long policyTypeId,
                                                                                     Long paymentTypeId,
                                                                                     LocalDate start,
                                                                                     LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp " +
            "JOIN pp.policy p " +
            "WHERE (p.insurer.id = :insurerId) " +
            "AND (p.stopDate IS NULL) " +
            "AND (:policyTypeId IS NULL OR p.policyType.id = :policyTypeId) " +
            "AND (:paymentTypeId IS NULL OR p.paymentType.id = :paymentTypeId) " +
            "AND (p.conclusionDate BETWEEN :start AND :end)" +
            "AND pp.datePaid IS NOT NULL")
    List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Paid(Long insurerId,
                                                                                      Long policyTypeId,
                                                                                      Long paymentTypeId,
                                                                                      LocalDate start,
                                                                                      LocalDate end);

    @Query("SELECT pp FROM PolicyPart pp " +
            "JOIN pp.policy p " +
            "WHERE (p.insurer.id = :insurerId) " +
            "AND (p.stopDate IS NULL) " +
            "AND (:policyTypeId IS NULL OR p.policyType.id = :policyTypeId) " +
            "AND (:paymentTypeId IS NULL OR p.paymentType.id = :paymentTypeId) " +
            "AND (p.conclusionDate BETWEEN :start AND :end)" +
            "AND pp.datePaid IS NULL")
    List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Debt(Long insurerId,
                                                                                      Long policyTypeId,
                                                                                      Long paymentTypeId,
                                                                                      LocalDate start,
                                                                                      LocalDate end);

    //INSURER REPORT CONTROL
    @Query("SELECT pp FROM PolicyPart pp JOIN pp.policy p JOIN p.insurer i JOIN p.client c JOIN p.policyType pt " +
            "WHERE i.id = :insurerId AND pp.date <= :date " +
            "AND (p.stopDate IS NULL) " +
            "AND (p.policyNumber LIKE %:policyNumber% OR :policyNumber IS NULL) " +
            "AND (c.name LIKE %:clientName% OR :clientName IS NULL) " +
            "AND (pt.id = :policyTypeId OR :policyTypeId IS NULL) " +
            "AND ((pp.sumReal = 0 OR pp.sumReal IS NULL) OR (pp.datePaid IS NULL) OR (pp.dateConfirmed IS NULL)) " +
            "ORDER BY pp.date ASC")
    List<PolicyPart> findPolicyPartsByInsurerAndDate_All(@Param("insurerId") Long insurerId,
                                                         @Param("date") LocalDate date,
                                                         @Param("policyNumber") String policyNumber,
                                                         @Param("clientName") String clientName,
                                                         @Param("policyTypeId") Long policyTypeId);

    @Query("SELECT pp FROM PolicyPart pp JOIN pp.policy p JOIN p.insurer i JOIN p.client c JOIN p.policyType pt " +
            "WHERE i.id = :insurerId AND pp.date <= :date " +
            "AND (p.stopDate IS NULL) " +
            "AND (p.policyNumber LIKE %:policyNumber% OR :policyNumber IS NULL) " +
            "AND (c.name LIKE %:clientName% OR :clientName IS NULL) " +
            "AND (pt.id = :policyTypeId OR :policyTypeId IS NULL) " +
            "AND (pp.dateConfirmed IS NULL)" +
            "AND (pp.sumReal > 0)" +
            "ORDER BY pp.date ASC")
    List<PolicyPart> findPolicyPartsByInsurerAndDate_Paid(@Param("insurerId") Long insurerId,
                                                          @Param("date") LocalDate date,
                                                          @Param("policyNumber") String policyNumber,
                                                          @Param("clientName") String clientName,
                                                          @Param("policyTypeId") Long policyTypeId);

    @Query("SELECT pp FROM PolicyPart pp JOIN pp.policy p JOIN p.insurer i JOIN p.client c JOIN p.policyType pt " +
            "WHERE i.id = :insurerId AND pp.date <= :date " +
            "AND (p.stopDate IS NULL) " +
            "AND (p.policyNumber LIKE %:policyNumber% OR :policyNumber IS NULL) " +
            "AND (c.name LIKE %:clientName% OR :clientName IS NULL) " +
            "AND (pt.id = :policyTypeId OR :policyTypeId IS NULL) " +
            "AND (pp.dateConfirmed IS NULL)" +
            "AND (pp.sumReal = 0)" +
            "ORDER BY pp.date ASC")
    List<PolicyPart> findPolicyPartsByInsurerAndDate_Debt(@Param("insurerId") Long insurerId,
                                                          @Param("date") LocalDate date,
                                                          @Param("policyNumber") String policyNumber,
                                                          @Param("clientName") String clientName,
                                                          @Param("policyTypeId") Long policyTypeId);
}