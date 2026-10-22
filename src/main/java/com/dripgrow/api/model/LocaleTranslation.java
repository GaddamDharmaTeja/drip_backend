package com.dripgrow.api.model;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "locale_translations")
public class LocaleTranslation {

    @Id
    private String language;
    private Map<String, Object> translations = new LinkedHashMap<>();

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Map<String, Object> getTranslations() { return translations; }
    public void setTranslations(Map<String, Object> translations) { this.translations = translations; }
}
