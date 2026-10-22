package com.dripgrow.api.controller;

import com.dripgrow.api.service.LocaleTranslationService;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LocaleController {
    private final LocaleTranslationService service;
    public LocaleController(LocaleTranslationService service) { this.service = service; }

    @GetMapping("/locales/{language}")
    public Map<String, Object> publicLocale(@PathVariable String language) { return service.get(language); }

    @GetMapping("/locales")
    public List<String> languages() { return service.listLanguages(); }

    @GetMapping("/admin/locales/{language}")
    public Map<String, Object> adminLocale(@PathVariable String language) { return service.get(language); }

    @PutMapping("/admin/locales/{language}")
    public Map<String, Object> updateLocale(@PathVariable String language, @RequestBody Map<String, Object> body) {
        return service.save(language, body);
    }
}
