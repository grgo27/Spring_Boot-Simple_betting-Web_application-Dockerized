package com.vjezbanje.betting_app.controller;

import com.vjezbanje.betting_app.DTO.WebUser;
import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }


    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {


        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/register")
    public String showRegisterForm(Model theModel){

        WebUser webUser = new WebUser();

        theModel.addAttribute("webUser", webUser);

        return "register-form";
    }

    @PostMapping("/processRegisterForm")
    public String processRegisterForm(@Valid @ModelAttribute("webUser") WebUser theWebUser, BindingResult theBindingResult, Model theModel, HttpSession session){

        String theUsername = theWebUser.getUsername(); // dohvacamo username od webusera kojeg smo ispunili formom

        if(theBindingResult.hasErrors()){ // rezultat validacije ce bit u binding resultu i ako sadrzi errore ponovo nas preusjmejri na register-form
            return "register-form";
        }

        // provjerit da li novi user kojeg pokusavamo registrirat vec postoji u bazi
        User existingUser = userService.findByUsername(theUsername);
        if(existingUser!=null){ // ako nije null znaci da vec postoji taj user
            theModel.addAttribute("webUser",new WebUser());
            // mogu i ne moran koristit ovu liniju, jer ako ne dodan novog web usera u model, gori san zada da koristimo u modelu vec postojeceg (ispunjenog web usera) i onda polja formi nece bit prazna kada ponovo odemo na registration-form.html
            theModel.addAttribute("regError","User name already exists."); // dodat poruku

            return "register-form"; // preusmjerit na register-form da ponovo ispunimo formu
        }

        userService.save(theWebUser); // spremit webUsera u bazu

        return "register-confirmation";


    }


}
