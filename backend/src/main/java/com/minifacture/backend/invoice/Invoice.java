package com.minifacture.backend.invoice;

import com.minifacture.backend.client.Client;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String number; // null while DRAFT

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<InvoiceLine> lines = new ArrayList<>();

    protected Invoice() {
    }

    public Invoice(Client client, LocalDate dueDate) {
        this.client = client;
        this.dueDate = dueDate;
    }

    public void changeDueDate(LocalDate dueDate) {
        requireDraft("modified");
        this.dueDate = dueDate;
    }

    public void clearLines() {
        requireDraft("modified");
        lines.clear();
    }

    public void addLine(String description, BigDecimal quantity,
            BigDecimal unitPriceExclTax, BigDecimal vatRate) {
        requireDraft("modified");
        lines.add(new InvoiceLine(this, lines.size() + 1, description,
                quantity, unitPriceExclTax, vatRate));
    }

    public void send(String number, LocalDate today) {
        requireDraft("sent");
        if (lines.isEmpty()) {
            throw new InvalidInvoiceStateException("An invoice without lines cannot be sent");
        }
        if (dueDate.isBefore(today)) {
            throw new InvalidInvoiceStateException("The due date is before the issue date");
        }
        this.number = number;
        this.issueDate = today;
        this.status = InvoiceStatus.SENT;
    }

    public void markPaid() {
        if (status != InvoiceStatus.SENT) {
            throw new InvalidInvoiceStateException("Only a sent invoice can be marked as paid");
        }
        this.status = InvoiceStatus.PAID;
    }

    public void requireDraft(String action) {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException("Only a draft can be " + action);
        }
    }

    public boolean isOverdue(LocalDate today) {
        return status == InvoiceStatus.SENT && dueDate.isBefore(today);
    }


    public BigDecimal totalExclTax() {
        return lines.stream().map(InvoiceLine::totalExclTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalVat() {
        return lines.stream().map(InvoiceLine::vatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalInclTax() {
        return totalExclTax().add(totalVat());
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Client getClient() {
        return client;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }
}