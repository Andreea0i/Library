package view.model.sale;

import model.validator.Notification;

import java.util.List;
import java.util.Map;

public interface SaleService {
    Notification<Boolean> sellBook(Long bookId, Long employeeId, Integer quantity, Double price);
    List<Sale> getAllSales();
    List<Sale> getSalesLastMonth();
    Map<String, Object> getMonthlyReportData();
    Double getTotalSalesValueLastMonth();
    Integer getTotalBooksSoldLastMonth();
    List<Sale> getSalesByEmployeeLastMonth(Long employeeId);

}
