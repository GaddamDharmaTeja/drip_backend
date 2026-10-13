package com.dripgrow.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "site_contact")
public class SiteContact {

    public static final String DEFAULT_ID = "main";

    @Id
    private String id = DEFAULT_ID;

    private String title = "DripGrow HQ";
    private String address = "12 Agri Tech Park, Nashik Road, Maharashtra 422101";
    private String email = "hello@dripgrow.example";
    private String phone = "+91 98765 43210";
    private String hours = "Mon–Sat, 9:00–18:00 IST";

    public static SiteContact defaults() {
        return new SiteContact();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
}
