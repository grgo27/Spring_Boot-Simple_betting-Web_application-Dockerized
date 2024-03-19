package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.DTO.UpdateUser;
import com.vjezbanje.betting_app.DTO.WebUser;
import com.vjezbanje.betting_app.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;

public interface UserService extends UserDetailsService {


    User findByUsername(String username);

    void save(WebUser webUser); // kao parametar stavljamo webusera

    void updateUser(UpdateUser updateUser);

    void depositMoney(String theUserName, BigDecimal amount);

    void withdrawMoney(String theUserName, BigDecimal amount);

    UpdateUser createUpdateUser(User theUser);
}
