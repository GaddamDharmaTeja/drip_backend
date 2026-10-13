package com.dripgrow.api.service;

import com.dripgrow.api.dto.ContactRequest;
import com.dripgrow.api.dto.CreateRequestDto;
import com.dripgrow.api.model.ContactMessage;
import com.dripgrow.api.repository.ContactMessageRepository;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactMessageRepository repository;
    private final RequestQuotationService requestQuotationService;

    public ContactService(
            ContactMessageRepository repository,
            RequestQuotationService requestQuotationService
    ) {
        this.repository = repository;
        this.requestQuotationService = requestQuotationService;
    }

    public Map<String, String> save(ContactRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setMessage(request.getMessage());
        message.setCreatedAt(Instant.now());
        ContactMessage saved = repository.save(message);

        CreateRequestDto dto = new CreateRequestDto();
        dto.setType("CONTACT");
        dto.setName(request.getName());
        dto.setEmail(request.getEmail());
        dto.setMessage(request.getMessage());
        requestQuotationService.createRequest(dto);

        return Map.of("id", saved.getId(), "status", "received");
    }
}
