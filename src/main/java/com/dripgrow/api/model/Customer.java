package com.dripgrow.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "customers")
public class Customer {
    @Id private String id;
    private String fullName;
    @Indexed(unique = true) private String phone;
    @Indexed(unique = true, sparse = true) private String email;
    private String passwordHash;
    private boolean active = true;
    private List<CustomerAddress> addresses = new ArrayList<>();
    private String defaultAddressId;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<CustomerAddress> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        return addresses;
    }
    public void setAddresses(List<CustomerAddress> addresses) { this.addresses = addresses == null ? new ArrayList<>() : addresses; }
    public String getDefaultAddressId() { return defaultAddressId; }
    public void setDefaultAddressId(String defaultAddressId) { this.defaultAddressId = defaultAddressId; }
}
