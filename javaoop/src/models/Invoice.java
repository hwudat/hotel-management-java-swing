package models;
import java.util.Date;

public class Invoice {
    private String invoiceID;
    private String bookingID;
    private String paymentStatus;
    private Date paymentDate;
    private double totalAmount;

    public Invoice(String id, String bookingID, String status, double total) {
        this.invoiceID = id;
        this.bookingID = bookingID;
        this.paymentStatus = status;
        this.totalAmount = total;
        this.paymentDate = new Date();
    }

    public void printInvoice() {
        System.out.println("--- INVOICE " + invoiceID + " ---");
        System.out.println("Booking Ref: " + bookingID);
        System.out.println("Total Paid: " + totalAmount);
        System.out.println("Status: " + paymentStatus);
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}