package com.dripgrow.api.service;

import com.dripgrow.api.model.SiteContact;
import com.dripgrow.api.repository.SiteContactRepository;
import org.springframework.stereotype.Service;

@Service
public class SiteContactService {

    private final SiteContactRepository siteContactRepository;

    public SiteContactService(SiteContactRepository siteContactRepository) {
        this.siteContactRepository = siteContactRepository;
    }

    public SiteContact get() {
        return siteContactRepository.findById(SiteContact.DEFAULT_ID)
                .orElseGet(SiteContact::defaults);
    }

    public SiteContact save(SiteContact incoming) {
        SiteContact current = get();
        current.setId(SiteContact.DEFAULT_ID);
        if (incoming.getTitle() != null) {
            current.setTitle(incoming.getTitle().trim());
        }
        if (incoming.getAddress() != null) {
            current.setAddress(incoming.getAddress().trim());
        }
        if (incoming.getEmail() != null) {
            current.setEmail(incoming.getEmail().trim());
        }
        if (incoming.getPhone() != null) {
            current.setPhone(incoming.getPhone().trim());
        }
        if (incoming.getHours() != null) {
            current.setHours(incoming.getHours().trim());
        }
        return siteContactRepository.save(current);
    }

    public void ensureDefaults() {
        if (!siteContactRepository.existsById(SiteContact.DEFAULT_ID)) {
            siteContactRepository.save(SiteContact.defaults());
        }
    }
}
