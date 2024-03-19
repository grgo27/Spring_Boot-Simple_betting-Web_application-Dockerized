package com.vjezbanje.betting_app.security;

import com.vjezbanje.betting_app.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) { // injecting userService into the authenticationProvider method is necessary
        // for configuring the DaoAuthenticationProvider with a custom UserDetailsService, which is essential for authenticating users in your Spring Security setup.
        // ovvaj custom UserDetailService smo napravili u UserServiceImpl gdje smo sa loadbyusername vratili UserDetail objekt
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {

        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/register").permitAll() // ode bi mi bilo pametinije da san koristija /register/** jer to onda vridi za sve subpathove od registera i
                                // onda ne bi mora koristit ovaj processregister ispod. ali to podrazumijeva da u register controlleru napravin glavni requestmapping na /register i
                                // onda da svi sub request mappingsi ( viewsi) imaju svoj dodatni url. al s obzirom da bi stavija ode /register/** sve te dodatne subpathove bi uvatilo sa ovin permit all
                                .requestMatchers("/processRegisterForm").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/showBets").permitAll()//.authenticated() // iman dvi opcije sta cu sa viewvima. mogu stavit ode da triba login za njega sa .authenticated, pa da pristupin tom pageu odvest ce me na login page ako nisan ulogiran. ili da permitan  pa onda di je link za welcome, stavin da se link vidi samo ako smo ulogirani. to cu i napravit
                                .requestMatchers("/account").permitAll()
                                .requestMatchers("/processBet").permitAll()
                                .anyRequest().authenticated()

                        // iza ovih request matchera ili anyrequesta mora bt ili permital ili authenticated inace nece valjat

                )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .successHandler(customAuthenticationSuccessHandler)
                                .permitAll()
                )
                .logout(logout -> logout.invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // kada stavin ovo onda ne moran koristit forme za logoutanje, vec ode definiran koji url moran unit za logoutanje i onda se mogu logoutat korsteci linkove na ovaj url. spring odradi logoutanje u pozadini samo se tribamo pozvat na ovi url /logout
                        //  nastavak na new antPath: this part of code configures Spring Security to use the "/logout" URL as the endpoint for logging out users. When a request is made to this URL, Spring Security will handle it as a logout request.  In this case, it specifies that the logout functionality should be triggered when a request is made to the "/logout" endpoint
                        .permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );

        return http.build();
    }


}
