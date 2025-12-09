package view.model.sale;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SaleRepository {
    boolean save(Sale sale);
    List<Sale> findAll();
    List<Sale> findByBookId(Long bookId);
    List<Sale> findByEmployeeId(Long employeeId);
    List<Sale> findSalesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    List<Sale> findSalesLastMonth();
    Double getTotalSalesValueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    Integer getTotalBooksSoldBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}
