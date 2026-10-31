package com.minifacture.backend;

import com.minifacture.backend.client.Client;
import com.minifacture.backend.invoice.InvalidInvoiceStateException;
import com.minifacture.backend.invoice.Invoice;
import com.minifacture.backend.invoice.InvoiceLine;
import com.minifacture.backend.invoice.InvoiceStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
    private static final LocalDate DUE = LocalDate.of(2026, 12, 31);

    private Invoice emptyDraft() {
        return new Invoice(new Client("dupont", "dupont@gmail.com", "1 rue de Paris"), DUE);
    }

    private Invoice draftWithOneLine() {
        Invoice invoice = emptyDraft();
        invoice.addLine("Dev Angular", bd("1"), bd("100.00"), bd("20"));
        return invoice;
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    @Test
    @DisplayName("Totals are computed from the lines")
    void computesTotals() {
        Invoice invoice = emptyDraft();
        invoice.addLine("Dev Angular", bd("3"), bd("450.00"), bd("20"));
        invoice.addLine("Hosting", bd("1"), bd("50.00"), bd("20"));

        assertThat(invoice.totalExclTax()).isEqualByComparingTo("1400.00");
        assertThat(invoice.totalVat()).isEqualByComparingTo("280.00");
        assertThat(invoice.totalInclTax()).isEqualByComparingTo("1680.00");
    }

    @Test
    @DisplayName("An invoice without lines has zero totals")
    void emptyInvoiceHasZeroTotals() {
        Invoice invoice = emptyDraft();

        assertThat(invoice.totalExclTax()).isEqualByComparingTo("0");
        assertThat(invoice.totalInclTax()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Mixed VAT rates are summed line by line")
    void handlesMixedVatRates() {
        Invoice invoice = emptyDraft();
        invoice.addLine("Service", bd("1"), bd("100.00"), bd("20"));
        invoice.addLine("Book", bd("2"), bd("10.00"), bd("5.5"));

        assertThat(invoice.totalVat()).isEqualByComparingTo("21.10");
        assertThat(invoice.totalInclTax()).isEqualByComparingTo("141.10");
    }

    @Test
    @DisplayName("Fractional quantities are supported (1.5 days)")
    void supportsFractionalQuantity() {
        Invoice invoice = emptyDraft();
        invoice.addLine("Consulting", bd("1.5"), bd("400.00"), bd("20"));

        assertThat(invoice.totalExclTax()).isEqualByComparingTo("600.00");
    }

    @Test
    @DisplayName("VAT is rounded HALF_UP to 2 decimals")
    void roundsVatHalfUp() {
        Invoice invoice = emptyDraft();
        invoice.addLine("Tiny item", bd("1"), bd("0.10"), bd("5"));

        assertThat(invoice.totalVat()).isEqualByComparingTo("0.01");
    }

    @Test
    @DisplayName("Line positions are sequential")
    void numbersLinesSequentially() {
        Invoice invoice = emptyDraft();
        invoice.addLine("A", bd("1"), bd("1"), bd("20"));
        invoice.addLine("B", bd("1"), bd("1"), bd("20"));

        assertThat(invoice.getLines()).extracting(InvoiceLine::getPosition).containsExactly(1, 2);
    }

    // ---------- Sending ----------

    @Test
    @DisplayName("Sending a draft sets number, issue date and status")
    void sendsDraft() {
        Invoice invoice = draftWithOneLine();

        invoice.send("FAC-2026-0001", TODAY);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.SENT);
        assertThat(invoice.getNumber()).isEqualTo("FAC-2026-0001");
        assertThat(invoice.getIssueDate()).isEqualTo(TODAY);
    }

    @Test
    @DisplayName("A new invoice is a draft without number")
    void startsAsDraft() {
        Invoice invoice = emptyDraft();

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(invoice.getNumber()).isNull();
        assertThat(invoice.getIssueDate()).isNull();
    }

    @Test
    @DisplayName("An invoice without lines cannot be sent and stays a draft")
    void refusesToSendWithoutLines() {
        Invoice invoice = emptyDraft();

        assertThatThrownBy(() -> invoice.send("FAC-2026-0001", TODAY))
                .isInstanceOf(InvalidInvoiceStateException.class)
                .hasMessageContaining("without lines");

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(invoice.getNumber()).isNull();
    }

    @Test
    @DisplayName("An invoice cannot be sent twice")
    void refusesToSendTwice() {
        Invoice invoice = draftWithOneLine();
        invoice.send("FAC-2026-0001", TODAY);

        assertThatThrownBy(() -> invoice.send("FAC-2026-0002", TODAY))
                .isInstanceOf(InvalidInvoiceStateException.class);

        assertThat(invoice.getNumber()).isEqualTo("FAC-2026-0001");
    }

    @Test
    @DisplayName("An invoice whose due date is already past cannot be sent")
    void refusesToSendWithPastDueDate() {
        Invoice invoice = draftWithOneLine();
        invoice.changeDueDate(LocalDate.of(2026, 10, 1));

        assertThatThrownBy(() -> invoice.send("FAC-2026-0001", TODAY))
                .isInstanceOf(InvalidInvoiceStateException.class)
                .hasMessageContaining("due date");
    }

    @Test
    @DisplayName("An invoice due today can be sent")
    void allowsDueDateEqualToToday() {
        Invoice invoice = draftWithOneLine();
        invoice.changeDueDate(TODAY);

        invoice.send("FAC-2026-0001", TODAY);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.SENT);
    }

    @Test
    @DisplayName("A sent invoice cannot be modified")
    void locksSentInvoice() {
        Invoice invoice = draftWithOneLine();
        invoice.send("FAC-2026-0001", TODAY);

        assertThatThrownBy(() -> invoice.addLine("Extra", bd("1"), bd("10"), bd("20")))
                .isInstanceOf(InvalidInvoiceStateException.class);
        assertThatThrownBy(() -> invoice.changeDueDate(DUE.plusDays(10)))
                .isInstanceOf(InvalidInvoiceStateException.class);
        assertThatThrownBy(invoice::clearLines)
                .isInstanceOf(InvalidInvoiceStateException.class);

        assertThat(invoice.getLines()).hasSize(1);
    }

    @Test
    @DisplayName("A draft can be modified freely")
    void allowsModifyingDraft() {
        Invoice invoice = draftWithOneLine();

        invoice.clearLines();
        invoice.addLine("New line", bd("2"), bd("300.00"), bd("20"));
        invoice.changeDueDate(DUE.plusDays(30));

        assertThat(invoice.getLines()).hasSize(1);
        assertThat(invoice.totalExclTax()).isEqualByComparingTo("600.00");
        assertThat(invoice.getDueDate()).isEqualTo(DUE.plusDays(30));
    }

    // ---------- Payment ----------

    @Test
    @DisplayName("A sent invoice can be marked as paid")
    void marksSentInvoiceAsPaid() {
        Invoice invoice = draftWithOneLine();
        invoice.send("FAC-2026-0001", TODAY);

        invoice.markPaid();

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
    }

    @Test
    @DisplayName("A draft cannot be marked as paid")
    void refusesToPayDraft() {
        Invoice invoice = draftWithOneLine();

        assertThatThrownBy(invoice::markPaid)
                .isInstanceOf(InvalidInvoiceStateException.class);
    }

    @Test
    @DisplayName("A paid invoice cannot be paid again")
    void refusesToPayTwice() {
        Invoice invoice = draftWithOneLine();
        invoice.send("FAC-2026-0001", TODAY);
        invoice.markPaid();

        assertThatThrownBy(invoice::markPaid)
                .isInstanceOf(InvalidInvoiceStateException.class);
    }

    // ---------- Overdue (derived, never stored) ----------

    @Test
    @DisplayName("A sent invoice past its due date is overdue")
    void sentAndPastDueIsOverdue() {
        Invoice invoice = draftWithOneLine();
        invoice.send("FAC-2026-0001", TODAY);

        assertThat(invoice.isOverdue(DUE.plusDays(1))).isTrue();
    }

    @Test
    @DisplayName("A sent invoice is not overdue on its due date")
    void notOverdueOnDueDate() {
        Invoice invoice = draftWithOneLine();
        invoice.send("FAC-2026-0001", TODAY);

        assertThat(invoice.isOverdue(DUE)).isFalse();
    }

    @Test
    @DisplayName("Drafts and paid invoices are never overdue")
    void draftAndPaidAreNeverOverdue() {
        Invoice draft = draftWithOneLine();
        assertThat(draft.isOverdue(DUE.plusDays(100))).isFalse();

        Invoice paid = draftWithOneLine();
        paid.send("FAC-2026-0001", TODAY);
        paid.markPaid();
        assertThat(paid.isOverdue(DUE.plusDays(100))).isFalse();
    }
}