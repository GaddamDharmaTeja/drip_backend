package com.dripgrow.api.service;

import com.dripgrow.api.dto.CreateRequestDto;
import com.dripgrow.api.model.Quotation;
import com.dripgrow.api.model.ServiceRequest;
import com.dripgrow.api.repository.QuotationRepository;
import com.dripgrow.api.repository.ServiceRequestRepository;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RequestQuotationService {

    private static final Set<String> REQUEST_TYPES =
            Set.of("CONTACT", "BULK_QUOTE", "INSTALLATION", "CALCULATOR");
    private static final Set<String> REQUEST_STATUSES = Set.of("New", "InProgress", "Closed");
    private static final Set<String> QUOTE_STATUSES = Set.of("Draft", "Sent", "Accepted", "Rejected");

    private final ServiceRequestRepository requestRepository;
    private final QuotationRepository quotationRepository;

    public RequestQuotationService(
            ServiceRequestRepository requestRepository,
            QuotationRepository quotationRepository
    ) {
        this.requestRepository = requestRepository;
        this.quotationRepository = quotationRepository;
    }

    public ServiceRequest createRequest(CreateRequestDto dto) {
        String type = dto.getType().trim().toUpperCase(Locale.ROOT);
        if (!REQUEST_TYPES.contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request type");
        }
        Instant now = Instant.now();
        ServiceRequest req = new ServiceRequest();
        req.setType(type);
        req.setName(dto.getName());
        req.setEmail(dto.getEmail());
        req.setPhone(dto.getPhone());
        req.setMessage(dto.getMessage());
        req.setDetails(dto.getDetails());
        req.setStatus("New");
        req.setCreatedAt(now);
        req.setUpdatedAt(now);
        return requestRepository.save(req);
    }

    public List<ServiceRequest> listRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    public ServiceRequest updateRequestStatus(String id, String status) {
        ServiceRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        if (!REQUEST_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        req.setStatus(status);
        req.setUpdatedAt(Instant.now());
        return requestRepository.save(req);
    }

    public Quotation createQuotation(Quotation quotation) {
        Instant now = Instant.now();
        quotation.setId(null);
        if (quotation.getStatus() == null || quotation.getStatus().isBlank()) {
            quotation.setStatus("Draft");
        }
        if (!QUOTE_STATUSES.contains(quotation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quotation status");
        }
        quotation.setCreatedAt(now);
        quotation.setUpdatedAt(now);
        return quotationRepository.save(quotation);
    }

    public Quotation convertRequestToQuotation(String requestId, double amount, String notes) {
        ServiceRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        Quotation q = new Quotation();
        q.setRequestId(req.getId());
        q.setCustomerName(req.getName());
        q.setEmail(req.getEmail());
        q.setPhone(req.getPhone());
        q.setAmount(amount);
        q.setNotes(notes != null ? notes : req.getMessage());
        q.setStatus("Draft");
        Quotation saved = createQuotation(q);
        req.setStatus("InProgress");
        req.setUpdatedAt(Instant.now());
        requestRepository.save(req);
        return saved;
    }

    public List<Quotation> listQuotations() {
        return quotationRepository.findAllByOrderByCreatedAtDesc();
    }

    public Quotation updateQuotation(String id, Quotation incoming) {
        Quotation existing = quotationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quotation not found"));
        if (incoming.getCustomerName() != null) {
            existing.setCustomerName(incoming.getCustomerName());
        }
        if (incoming.getPhone() != null) {
            existing.setPhone(incoming.getPhone());
        }
        if (incoming.getEmail() != null) {
            existing.setEmail(incoming.getEmail());
        }
        if (incoming.getNotes() != null) {
            existing.setNotes(incoming.getNotes());
        }
        if (incoming.getAmount() > 0) {
            existing.setAmount(incoming.getAmount());
        }
        if (incoming.getStatus() != null) {
            if (!QUOTE_STATUSES.contains(incoming.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quotation status");
            }
            existing.setStatus(incoming.getStatus());
        }
        existing.setUpdatedAt(Instant.now());
        return quotationRepository.save(existing);
    }
}
