package com.minifacture.backend.invoice;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @EntityGraph(attributePaths = { "client", "lines" })
    List<Invoice> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = { "client", "lines" })
    List<Invoice> findByClientIdOrderByCreatedAtDesc(Long clientId);

    @EntityGraph(attributePaths = { "client", "lines" })
    Optional<Invoice> findWithDetailsById(Long id);

    boolean existsByClientId(Long clientId);
}