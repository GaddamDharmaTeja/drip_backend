package com.dripgrow.api.service;

import com.dripgrow.api.model.LocaleTranslation;
import com.dripgrow.api.repository.LocaleTranslationRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LocaleTranslationService {
    private static final int MAX_JSON_LENGTH = 500_000;
    private final LocaleTranslationRepository repository;

    public LocaleTranslationService(LocaleTranslationRepository repository) { this.repository = repository; }

    public Map<String, Object> get(String language) {
        validateLanguage(language);
        return repository.findById(language).map(LocaleTranslation::getTranslations).orElseGet(LinkedHashMap::new);
    }

    public Map<String, Object> save(String language, Map<String, Object> translations) {
        validateLanguage(language);
        if (translations == null || translations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Translation file must contain a JSON object.");
        }
        if (translations.toString().length() > MAX_JSON_LENGTH || !isValid(translations)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Translations must be a JSON object containing only nested objects and text values.");
        }
        LocaleTranslation document = new LocaleTranslation();
        document.setLanguage(language);
        document.setTranslations(new LinkedHashMap<>(translations));
        return repository.save(document).getTranslations();
    }

    public List<String> listLanguages() {
        return repository.findAll().stream()
                .map(LocaleTranslation::getLanguage)
                .filter(language -> language != null && !language.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    private void validateLanguage(String language) {
        if (language == null || !language.matches("^[a-z]{2,3}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Use a two- or three-letter lowercase language code, for example en, te, or hi.");
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isValid(Map<String, Object> value) {
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            if (entry.getKey().isBlank() || entry.getValue() == null) return false;
            if (entry.getValue() instanceof String) continue;
            if (entry.getValue() instanceof Map<?, ?> nested) {
                if (!nested.keySet().stream().allMatch(String.class::isInstance)
                        || !isValid((Map<String, Object>) nested)) return false;
                continue;
            }
            return false;
        }
        return true;
    }
}
