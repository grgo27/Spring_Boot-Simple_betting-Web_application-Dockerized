package com.vjezbanje.betting_app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class WebUser {

    @NotNull(message = "You need to fill up this field")
    @Size(min = 1, message = "You need to fill up this field")
    private String username;

    @NotNull(message = "You need to fill up this field")
    @Size(min = 1, message = "You need to fill up this field")
    private String password;

    @NotNull(message = "You need to fill up this field")
    @Size(min = 1, message = "You need to fill up this field")
    private String firstName;

    @NotNull(message = "You need to fill up this field")
    @Size(min = 1, message = "You need to fill up this field")
    private String lastName;

    @NotNull(message = "You need to fill up this field")
    @Size(min = 1, message = "You need to fill up this field")
    @Email
    private String email;



    public WebUser(){
    }

    public WebUser(String username, String password, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
