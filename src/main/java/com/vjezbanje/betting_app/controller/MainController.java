package com.vjezbanje.betting_app.controller;

import com.vjezbanje.betting_app.DTO.BetDTO;
import com.vjezbanje.betting_app.DTO.UpdateUser;
import com.vjezbanje.betting_app.entity.Bet;
import com.vjezbanje.betting_app.entity.Game;
import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.pomocno.BetResult;
import com.vjezbanje.betting_app.service.BetService;
import com.vjezbanje.betting_app.service.GameService;
import com.vjezbanje.betting_app.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

    private UserService userService;
    private BetService betService;
    private GameService gameService;

    private boolean paymentAdded = false;

    public static final int YEAR = 2024;
    public static final int MONTH = 3;
    public static final int DAY = 25;

    @Autowired
    public MainController(UserService userService, BetService betService, GameService gameService) {
        this.userService = userService;
        this.betService = betService;
        this.gameService = gameService;

    }

    @GetMapping("/")
    public String showHome(HttpSession session, Authentication authentication,Model theModel) {
        if(authentication!= null && authentication.isAuthenticated()){
            String userName = authentication.getName();

            User theUser = userService.findByUsername(userName);

            session.setAttribute("testUser",theUser);

            BetDTO betDTO = new BetDTO();

            // ako je user logiran dodat betDto u model za data binding sa formom
            theModel.addAttribute("betDTO",betDTO);
        }
        LocalDate today = LocalDate.now();

        LocalDate specificDate = LocalDate.of(YEAR,MONTH,DAY);

        if (today.isAfter(specificDate)) {
            betService.changeBetResult();
            gameService.deleteGames();

            if (!paymentAdded) { // if not payment added. znaci kad je payment added false /PODSJETNIK: !paymentAdded is equivalent to !false, because paymentAdded is false,!false evaluates to true because ! negates the value. So, !paymentAdded evaluates to true, Since the condition (!paymentAdded) evaluates to true, the code block associated with the if statement will be executed.
                List<Bet> theBets = betService.findAllBets();
                for (Bet tempBet : theBets) {
                    if (tempBet.getResult() == BetResult.WINNING) {
                        betService.addPaymentToBalance(tempBet);
                    }
                }
                paymentAdded = true; // POSTAVLJAN ZA TRUE I ONDA KADA SE OPET BUDE PROVJERAVA OVI GORI UVJET, BIT CE FALSE JE NOT TRUE DAJE FALSE I ONDA SE NECE EXECUTAT TO U IF STATMENTU
                return "redirect:/";
            }
        }

        List<Game> theGames = gameService.findAll();

        theModel.addAttribute("games",theGames);

        return "home";
    }

    @PostMapping("/processBet")
    public String processBet(@Valid @ModelAttribute("betDTO") BetDTO betDTO, BindingResult theBindingResult, Model theModel,Authentication authentication){

        if (theBindingResult.hasErrors()){
            return "home";
        }

        System.out.println(betDTO);

        String username = authentication.getName();

        try {
            Bet theBet = betService.processBetDTO(betDTO, username);
            theModel.addAttribute("theBet", theBet); // mogu maknit ovo jer san posli doda ovaj showBet mapping i tamo cu prikazivat podatke od beta na htmlu
            return "redirect:/showBets";
        } catch (RuntimeException e){
            List<Game> games = gameService.findAll();
            theModel.addAttribute("games",games);
            theModel.addAttribute("errorMessage", e.getMessage());
            theModel.addAttribute("betDTO", new BetDTO());// u slucaju errora stavija san da u model dodamo novi BetDTO objet tako da forma bude prazna, moga san i bez ove linije pa bi dodalo onog BetDTO sta je definiran gori u parametrima u @ModelAttribute
            return "home";
        }

    }

    @GetMapping("/showBets")
    public String showBets(Model theModel, Authentication authentication) {

        String username = authentication.getName();

        User theUser = userService.findByUsername(username);

        List<Bet> theBets = betService.findAllBetsByUserId(theUser.getId());

        theModel.addAttribute("allBets", theBets);


        return "show-bets";

    }

    @GetMapping("/account")
    public String showAccount(@RequestParam(name = "username", required = false) String theUsername, Model theModel){

        if (theUsername == null || theUsername.isEmpty()) { // ovo san doda u slucaju ako user gori u url slucajno sam upise samo /account bez parametra da nas redirecta na forbidden acces. za ovo moran modificirat gori @RequestParam( dodat name= i da je required= false)
            return "redirect:/access-denied";
        }

        User theUser = userService.findByUsername(theUsername);

        UpdateUser theUpdateUser = userService.createUpdateUser(theUser);

        theModel.addAttribute("theUpdateUser",theUpdateUser);

        return "account";
    }

    @PostMapping("/updateUser")
    public String updateUser(@Valid @ModelAttribute("theUpdateUser") UpdateUser theUpdateUser, Model theModel, BindingResult theBindingResult){

        if(theBindingResult.hasErrors()){
            return "account";
        }
        System.out.println("Username: " + theUpdateUser.getUsername());
        System.out.println("Balance is: " + theUpdateUser.getBalance()); // OVO MI JE DOKAZ DA OVAJ theUpdateUser GORI U MODELATTRIBUTE SADRŽI SAMO ONE PODATKE STA SMO ISPUNILI FORMOM
        // I POVEZALI SA TIM OBJEKTOM. U FORMU NISAN STAVIJA BALANCE I ZATO KAD ODE PRINTAN BALANCE ON JE NULL
        // IAKO ZAPRAVO BALANCE U BAZI POSTOJI. ALI TAJ BALANCE PRIKO FORME NIJE POVEZAN SA OVIN THEUPDATEUSER OBJEKTON. da san stavija i <input type="hidden" th:field="*{balance}" /> onda bi ima balance
        userService.updateUser(theUpdateUser);

        return "redirect:/account?username=" + theUpdateUser.getUsername();
    }

    @PostMapping("/processDeposit")
    public String processDeposit(@RequestParam("username") String theUserName,
                                 @RequestParam("deposit")BigDecimal depositAmount){

        userService.depositMoney(theUserName,depositAmount);

        return "redirect:/account?username=" + theUserName;
    }

    @PostMapping("/processWithdrawal")
    public String processWithdrawal(@RequestParam("username") String theUserName,
                                 @RequestParam("withdraw")BigDecimal withdrawalAmount, Model theModel){

        try {
            userService.withdrawMoney(theUserName,withdrawalAmount);
            return "redirect:/account?username=" + theUserName;
        } catch (RuntimeException e){
            User theUser = userService.findByUsername(theUserName);

            UpdateUser theUpdateUser = userService.createUpdateUser(theUser);

            theModel.addAttribute("theUpdateUser",theUpdateUser);
            theModel.addAttribute("errorMessage", e.getMessage());

            return "account";
        }
    }

}

