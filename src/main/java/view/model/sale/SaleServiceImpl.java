package view.model.sale;

import model.Book;
//import model.Sale;
import model.validator.Notification;
import repository.book.BookRepository;
//import repository.sale.SaleRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import repository.user.UserRepository;


public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public SaleServiceImpl(SaleRepository saleRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository) {
        this.saleRepository = saleRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notification<Boolean> sellBook(Long bookId, Long employeeId, Integer quantity, Double unitPrice) {
        Notification<Boolean> notification = new Notification<>();

        // Validare
        if (quantity <= 0) {
            notification.addError("Quantity must be greater than 0");
            notification.setResult(false);
            return notification;
        }

        if (unitPrice <= 0) {
            notification.addError("Price must be greater than 0");
            notification.setResult(false);
            return notification;
        }

        // Verifică dacă cartea există
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            notification.addError("Book not found");
            notification.setResult(false);
            return notification;
        }

        Book book = bookOptional.get();

        // Verifică stoc
        if (book.getQuantity() < quantity) {
            notification.addError("Insufficient stock. Available: " + book.getQuantity());
            notification.setResult(false);
            return notification;
        }

        // Actualizează stocul cărții
        book.setQuantity(book.getQuantity() - quantity);
        boolean bookUpdated = bookRepository.update(book);

        if (!bookUpdated) {
            notification.addError("Failed to update book stock");
            notification.setResult(false);
            return notification;
        }

        // Calculează prețul total
        Double totalPrice = unitPrice * quantity;

        // Creează și salvează vânzarea
        Sale sale = new Sale(bookId, employeeId, quantity, totalPrice);


        boolean saleSaved = saleRepository.save(sale);

        if (!saleSaved) {
            notification.addError("Failed to save sale record");
            // Rollback stocul (opțional)
            book.setQuantity(book.getQuantity() + quantity);
            bookRepository.update(book);
            notification.setResult(false);
            return notification;
        }

        notification.setResult(true);
        return notification;
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public List<Sale> getSalesLastMonth() {
        return saleRepository.findSalesLastMonth();
    }

    @Override
    public Map<String, Object> getMonthlyReportData() {
        Map<String, Object> reportData = new HashMap<>();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);

        // Date de bază
        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);
        Double totalValue = saleRepository.getTotalSalesValueBetweenDates(startDate, endDate);
        Integer totalBooks = saleRepository.getTotalBooksSoldBetweenDates(startDate, endDate);

        reportData.put("sales", sales);
        reportData.put("totalValue", totalValue);
        reportData.put("totalBooks", totalBooks);
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);
        reportData.put("transactionCount", sales.size());

        // Statistici per angajat
        Map<Long, List<Sale>> salesByEmployee = sales.stream()
                .collect(Collectors.groupingBy(Sale::getEmployeeId));

        Map<String, Object> employeeStats = new HashMap<>();
        for (Map.Entry<Long, List<Sale>> entry : salesByEmployee.entrySet()) {
            Long employeeId = entry.getKey();
            List<Sale> employeeSales = entry.getValue();

            Double employeeTotal = employeeSales.stream()
                    .mapToDouble(Sale::getTotalPrice)
                    .sum();

            Integer employeeBooks = employeeSales.stream()
                    .mapToInt(Sale::getQuantity)
                    .sum();

            Map<String, Object> stats = new HashMap<>();
            stats.put("salesCount", employeeSales.size());
            stats.put("totalValue", employeeTotal);
            stats.put("booksSold", employeeBooks);

            // Adaugă numele angajatului dacă este disponibil
            // (ar trebui să ai un UserService pentru asta)

            employeeStats.put("Employee_" + employeeId, stats);
        }

        reportData.put("employeeStats", employeeStats);

        return reportData;
    }

    @Override
    public Double getTotalSalesValueLastMonth() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        return saleRepository.getTotalSalesValueBetweenDates(startDate, endDate);
    }

    @Override
    public Integer getTotalBooksSoldLastMonth() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        return saleRepository.getTotalBooksSoldBetweenDates(startDate, endDate);
    }

    @Override
    public List<Sale> getSalesByEmployeeLastMonth(Long employeeId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        List<Sale> allSales = saleRepository.findSalesBetweenDates(startDate, endDate);

        return allSales.stream()
                .filter(sale -> sale.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }
}
