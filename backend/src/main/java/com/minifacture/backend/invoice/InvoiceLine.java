package com.minifacture.backend.invoice;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "invoice_line")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price_excl_tax", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceExclTax;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate;

    protected InvoiceLine() {
    }

    InvoiceLine(Invoice invoice, int position, String description,
            BigDecimal quantity, BigDecimal unitPriceExclTax, BigDecimal vatRate) {
        this.invoice = invoice;
        this.position = position;
        this.description = description;
        this.quantity = quantity;
        this.unitPriceExclTax = unitPriceExclTax;
        this.vatRate = vatRate;
    }

    public BigDecimal totalExclTax() {
        return unitPriceExclTax.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal vatAmount() {
        return totalExclTax().multiply(vatRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalInclTax() {
        return totalExclTax().add(vatAmount());
    }

    public Long getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceExclTax() {
        return unitPriceExclTax;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }
}