package com.minifacture.backend.invoice;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_sequence")
public class InvoiceSequence {

    @Id
    @Column(name = "sequence_year")
    private int sequenceYear;

    @Column(name = "last_number", nullable = false)
    private int lastNumber;

    protected InvoiceSequence() {
    }

    public InvoiceSequence(int sequenceYear) {
        this.sequenceYear = sequenceYear;
    }

    public int next() {
        return ++lastNumber;
    }
}