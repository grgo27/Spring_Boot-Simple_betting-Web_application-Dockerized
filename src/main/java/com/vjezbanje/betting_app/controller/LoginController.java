package com.vjezbanje.betting_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLogin(){

        return "login-form";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied(){

        return "access-denied";
    }
}
