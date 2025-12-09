package view.model.sale;

import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Sale {
    private Long id;
    private Long bookId;
    private Long employeeId;    // cine a vândut
    private Long customerId;    // cine a cumpărat (poate fi null dacă e vânzare generală)
    private Integer quantity;
    private Double totalPrice;
    private LocalDateTime saleDate;

    public Sale() {}

    public Sale(Long bookId, Long employeeId, Integer quantity, Double totalPrice) {
        this.bookId = bookId;
        this.employeeId = employeeId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.saleDate = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    @Override
    public String toString() {
        return "Sale{id=" + id + ", bookId=" + bookId + ", employeeId=" + employeeId +
                ", quantity=" + quantity + ", totalPrice=" + totalPrice +
                ", saleDate=" + saleDate + "}";
    }
}
