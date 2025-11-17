package com.example.skillocal_final;

public class User {
    private String name;
    private String email;
    private String address;
    private String role;

    public User(String name, String email, String address, String role) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(String role) { this.role = role; }
}
