//package view.model;
//
//package
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfWriter;
//import service.sale.SaleService;
//import view.model.sale.Sale;
//
//import java.io.FileOutputStream;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class AdminService {
//
//    private final SaleService saleService;
//
//    public AdminService(SaleService saleService) {
//        this.saleService = saleService;
//    }
//
//    public void generateMonthlyReport(String fileName) {
//        LocalDateTime end = LocalDateTime.now();
//        LocalDateTime start = end.minusMonths(1);
//
//        List<Sale> sales = saleService.findSalesBetweenDates(start, end);
//
//        Document doc = new Document();
//        try {
//            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
//            doc.open();
//
//            doc.add(new Paragraph("RAPORT VANZARI — Ultimele 30 de zile\n\n"));
//
//            int totalBooks = 0;
//            double totalValue = 0;
//
//            for (Sale s : sales) {
//                String line = "Book ID: " + s.getBookId()
//                        + " | Quantity: " + s.getQuantity()
//                        + " | Total price: " + s.getTotalPrice()
//                        + " | Employee: " + s.getEmployeeId()
//                        + " | Customer: " + s.getCustomerId()
//                        + " | Date: " + s.getSaleDate();
//
//                doc.add(new Paragraph(line));
//
//                totalBooks += s.getQuantity();
//                totalValue += s.getTotalPrice();
//            }
//
//            doc.add(new Paragraph("\n---------------------"));
//            doc.add(new Paragraph("Total carti vandute: " + totalBooks));
//            doc.add(new Paragraph("Valoare totala: " + totalValue + " RON"));
//
//            doc.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
