package com.minifacture.backend.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
        @NotNull Long clientId,
        @NotNull LocalDate dueDate,
        @NotNull @Valid List<LineRequest> lines) {

    public record LineRequest(
            @NotBlank String description,
            @NotNull @Positive BigDecimal quantity,
            @NotNull @PositiveOrZero BigDecimal unitPriceExclTax,
            @NotNull @PositiveOrZero BigDecimal vatRate) {
    }
}