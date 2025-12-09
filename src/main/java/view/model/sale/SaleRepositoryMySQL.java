package view.model.sale;

import model.Book;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//import view.model.sale;

//import static database.Constants.Tables.SALE;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepositoryMySQL implements SaleRepository {

    private final Connection connection;

    public SaleRepositoryMySQL(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    // SaleRepositoryMySQL.java - modifică createTableIfNotExists()
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS sale (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "book_id INT NOT NULL," +
                "employee_id INT NOT NULL," +
                "customer_id INT," +
                "quantity INT NOT NULL," +
                "total_price DECIMAL(10,2) NOT NULL," +
                "sale_date DATETIME NOT NULL," +
                "FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE," +
                "FOREIGN KEY (employee_id) REFERENCES user(id) ON DELETE CASCADE," +
                "FOREIGN KEY (customer_id) REFERENCES user(id) ON DELETE SET NULL" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("✓ Sale table created/verified successfully");
        } catch (SQLException e) {
            System.err.println("✗ Error creating sale table: " + e.getMessage());
            e.printStackTrace();

            // Încearcă o alternativă dacă prima nu merge
            tryFallbackTableCreation();
        }
    }

    private void tryFallbackTableCreation() {
        // Alternativă: creează tabela fără foreign keys temporar
        String fallbackSql = "CREATE TABLE IF NOT EXISTS sale (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "book_id INT NOT NULL," +
                "employee_id INT NOT NULL," +
                "customer_id INT," +
                "quantity INT NOT NULL," +
                "total_price DECIMAL(10,2) NOT NULL," +
                "sale_date DATETIME NOT NULL" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(fallbackSql);
            System.out.println("✓ Sale table created without foreign keys (fallback)");
        } catch (SQLException e2) {
            System.err.println("✗ Critical: Could not create sale table: " + e2.getMessage());
        }
    }
    @Override
    public boolean save(Sale sale) {

        String sql = "INSERT INTO sale (book_id, employee_id, customer_id, quantity, total_price, sale_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, sale.getBookId());
            ps.setLong(2, sale.getEmployeeId());

            if (sale.getCustomerId() != null) {
                ps.setLong(3, sale.getCustomerId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, sale.getQuantity());
            ps.setDouble(5, sale.getTotalPrice());
            ps.setTimestamp(6, Timestamp.valueOf(sale.getSaleDate()));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    sale.setId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sale ORDER BY sale_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sales.add(extractSaleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public List<Sale> findByBookId(Long bookId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sale WHERE book_id = ? ORDER BY sale_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sales.add(extractSaleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public List<Sale> findByEmployeeId(Long employeeId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sale WHERE employee_id = ? ORDER BY sale_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sales.add(extractSaleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public List<Sale> findSalesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sale WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sales.add(extractSaleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public List<Sale> findSalesLastMonth() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        return findSalesBetweenDates(startDate, endDate);
    }

    @Override
    public Double getTotalSalesValueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(total_price) as total FROM sale WHERE sale_date BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public Integer getTotalBooksSoldBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(quantity) as total FROM sale WHERE sale_date BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Sale extractSaleFromResultSet(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getLong("id"));
        sale.setBookId(rs.getLong("book_id"));
        sale.setEmployeeId(rs.getLong("employee_id"));

        long customerId = rs.getLong("customer_id");
        if (!rs.wasNull()) {
            sale.setCustomerId(customerId);
        }

        sale.setQuantity(rs.getInt("quantity"));
        sale.setTotalPrice(rs.getDouble("total_price"));
        sale.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
        return sale;
    }


}