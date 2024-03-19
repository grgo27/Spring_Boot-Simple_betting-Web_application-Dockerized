package com.vjezbanje.betting_app.security;

import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler { // radimo custom radnje kad se uspjesno ulogiramo
    private UserService userService;

    @Autowired
    public CustomAuthenticationSuccessHandler(UserService theUserService) {
        userService = theUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        System.out.println("In customAuthenticationSuccessHandler");

        String userName = authentication.getName(); // In Spring Security, when a user is authenticated, the authentication object holds information about the authenticated user.
        // getName je samo jedna od metoda koju mozemo koristit na authentication objektu i ona radi sljedece: getName(): Returns the name of the authenticated principal (typically the username).

        System.out.println("userName=" + userName);

        User theUser = userService.findByUsername(userName);

        // now place in the session
        HttpSession session = request.getSession();
        session.setAttribute("testUser", theUser); // dodali smo logiranog usera u session i onda mozemo pozivat njegove fieldove recimo na home pageu jer tamo cemo bit redirectani

        // forward to home page
        response.sendRedirect(request.getContextPath() + "/"); // kada se uspjesno logiramo redireectaj nas na home page
    }
}
