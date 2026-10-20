package com.minifacture.backend.invoice;

import com.minifacture.backend.client.Client;
import com.minifacture.backend.client.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceSequenceRepository sequenceRepository;
    private final ClientRepository clientRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
            InvoiceSequenceRepository sequenceRepository,
            ClientRepository clientRepository) {
        this.invoiceRepository = invoiceRepository;
        this.sequenceRepository = sequenceRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> list(Long clientId) {
        LocalDate today = LocalDate.now();
        List<Invoice> invoices = clientId == null
                ? invoiceRepository.findAllByOrderByCreatedAtDesc()
                : invoiceRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        return invoices.stream().map(i -> InvoiceResponse.from(i, today)).toList();
    }

    @Transactional(readOnly = true)
    public InvoiceResponse get(Long id) {
        return InvoiceResponse.from(find(id), LocalDate.now());
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Invoice invoice = new Invoice(client, request.dueDate());
        applyLines(invoice, request);
        return InvoiceResponse.from(invoiceRepository.save(invoice), LocalDate.now());
    }

    @Transactional
    public InvoiceResponse update(Long id, InvoiceRequest request) {
        Invoice invoice = find(id);
        invoice.requireDraft("modified");
        invoice.changeDueDate(request.dueDate());
        invoice.clearLines();
        applyLines(invoice, request);
        return InvoiceResponse.from(invoice, LocalDate.now());
    }

    @Transactional
    public void delete(Long id) {
        Invoice invoice = find(id);
        invoice.requireDraft("deleted");
        invoiceRepository.delete(invoice);
    }

    @Transactional
    public InvoiceResponse send(Long id) {
        Invoice invoice = find(id);
        LocalDate today = LocalDate.now();

        // Row lock on the year's counter: concurrent sends wait for each other
        int year = today.getYear();
        InvoiceSequence sequence = sequenceRepository.findForUpdate(year)
                .orElseGet(() -> sequenceRepository.save(new InvoiceSequence(year)));

        String number = String.format("FAC-%d-%04d", year, sequence.next());
        invoice.send(number, today);
        return InvoiceResponse.from(invoice, today);
    }

    @Transactional
    public InvoiceResponse markPaid(Long id) {
        Invoice invoice = find(id);
        invoice.markPaid();
        return InvoiceResponse.from(invoice, LocalDate.now());
    }

    private Invoice find(Long id) {
        return invoiceRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    private void applyLines(Invoice invoice, InvoiceRequest request) {
        request.lines().forEach(l -> invoice.addLine(l.description(), l.quantity(), l.unitPriceExclTax(), l.vatRate()));
    }
}