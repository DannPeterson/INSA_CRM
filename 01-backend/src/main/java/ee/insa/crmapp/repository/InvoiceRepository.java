package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Invoice;
import ee.insa.crmapp.model.PolicyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Modifying
    @Query(value = "INSERT INTO invoice (id, invoice_number, text, conclusion_date, max_date, paid_date) VALUES (:id, :invoiceNumber, :text, :conclusionDate, :maxDate, :paidDate)", nativeQuery = true)
    void insertInvoiceWithId(
            @Param("id") Long id,
            @Param("invoiceNumber") String invoiceNumber,
            @Param("text") String text,
            @Param("conclusionDate") LocalDate conclusionDate,
            @Param("maxDate") LocalDate maxDate,
            @Param("paidDate") LocalDate paidDate
    );
}
