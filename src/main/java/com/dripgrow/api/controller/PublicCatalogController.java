package com.dripgrow.api.controller;

import com.dripgrow.api.dto.CreateRequestDto;
import com.dripgrow.api.model.IrrigationPlan;
import com.dripgrow.api.model.ProjectCase;
import com.dripgrow.api.model.ProjectPlan;
import com.dripgrow.api.model.ServiceRequest;
import com.dripgrow.api.model.SiteContact;
import com.dripgrow.api.service.CatalogAdminService;
import com.dripgrow.api.service.RequestQuotationService;
import com.dripgrow.api.service.SiteContactService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicCatalogController {

    private final CatalogAdminService catalogAdminService;
    private final RequestQuotationService requestQuotationService;
    private final SiteContactService siteContactService;

    public PublicCatalogController(
            CatalogAdminService catalogAdminService,
            RequestQuotationService requestQuotationService,
            SiteContactService siteContactService
    ) {
        this.catalogAdminService = catalogAdminService;
        this.requestQuotationService = requestQuotationService;
        this.siteContactService = siteContactService;
    }

    @GetMapping("/api/site-contact")
    public SiteContact siteContact() {
        return siteContactService.get();
    }

    @GetMapping("/api/irrigation-plans")
    public List<IrrigationPlan> irrigationPlans() {
        return catalogAdminService.listIrrigationPlans(true);
    }

    @GetMapping("/api/project-plans")
    public List<ProjectPlan> projectPlans() {
        return catalogAdminService.listProjectPlans(true);
    }

    @GetMapping("/api/projects")
    public List<ProjectCase> projects() {
        return catalogAdminService.listProjectCases(true);
    }

    @PostMapping("/api/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createRequest(@Valid @RequestBody CreateRequestDto dto) {
        ServiceRequest saved = requestQuotationService.createRequest(dto);
        return Map.of("id", saved.getId(), "status", saved.getStatus());
    }
}
