package com.sentinelcore.secureops.dto;

/**
 * Public registration payload.
 *
 * Security role is intentionally NOT part of this DTO. New accounts are
 * created as ROLE_VIEWER and privileged roles can only be assigned by an
 * administrator through the role-management endpoint.
 */
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String organization;

    public UserRegistrationRequest() {}

    public UserRegistrationRequest(String username, String password, String email,
                                   String firstName, String lastName, String phone,
                                   String organization) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.organization = organization;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
}
