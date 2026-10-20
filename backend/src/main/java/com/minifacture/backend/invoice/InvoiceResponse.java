package com.minifacture.backend.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        Long id,
        String number,
        Long clientId,
        String clientName,
        InvoiceStatus status,
        boolean overdue,
        LocalDate issueDate,
        LocalDate dueDate,
        List<LineResponse> lines,
        BigDecimal totalExclTax,
        BigDecimal totalVat,
        BigDecimal totalInclTax) {

    public record LineResponse(
            int position,
            String description,
            BigDecimal quantity,
            BigDecimal unitPriceExclTax,
            BigDecimal vatRate,
            BigDecimal totalExclTax) {
    }

    public static InvoiceResponse from(Invoice invoice, LocalDate today) {
        List<LineResponse> lines = invoice.getLines().stream()
                .map(l -> new LineResponse(l.getPosition(), l.getDescription(), l.getQuantity(),
                        l.getUnitPriceExclTax(), l.getVatRate(), l.totalExclTax()))
                .toList();

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getNumber(),
                invoice.getClient().getId(),
                invoice.getClient().getName(),
                invoice.getStatus(),
                invoice.isOverdue(today),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                lines,
                invoice.totalExclTax(),
                invoice.totalVat(),
                invoice.totalInclTax());
    }
}