package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.DAO.BetRepository;
import com.vjezbanje.betting_app.DAO.GameRepository;
import com.vjezbanje.betting_app.DAO.UserRepository;
import com.vjezbanje.betting_app.DTO.BetDTO;
import com.vjezbanje.betting_app.entity.GameForBet;
import com.vjezbanje.betting_app.entity.Bet;
import com.vjezbanje.betting_app.entity.Game;
import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.exception.GameNotFoundException;
import com.vjezbanje.betting_app.exception.NotEnoughFundsException;
import com.vjezbanje.betting_app.pomocno.BetResult;
import com.vjezbanje.betting_app.pomocno.RandomResultProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class BetServiceIMPL implements BetService {

    private UserRepository userRepository;
    private BetRepository betRepository;
    private GameRepository gameRepository;
    private RandomResultProvider randomResultProvider;

    public static final BigDecimal MIN_TAX_BASE = new BigDecimal("1327.23");
    public static final BigDecimal MEDIAN_TAX_BASE = new BigDecimal("3981.68");
    public static final BigDecimal MAX_TAX_BASE = new BigDecimal("66361.40");

    @Autowired
    public BetServiceIMPL(UserRepository userRepository, BetRepository betRepository, GameRepository gameRepository, RandomResultProvider randomResultProvider) {
        this.userRepository = userRepository;
        this.betRepository = betRepository;
        this.gameRepository = gameRepository;
        this.randomResultProvider = randomResultProvider;
    }

    @Override
    public Bet processBetDTO(BetDTO betDTO, String username) {

        // querijt usera iz baze na osnovu username koji stavimo u parametar
        User theUser = userRepository.findByUsername(username);

        // napravi novi bet object
        Bet theBet = new Bet();

        //dohvacan listu stringova [gameId1:selectedOdd1:1, gameId2:selectedOdd2:X itd]
        List<String> selectedOddsAndIds = betDTO.getSelectedOddsAndIds();

        // ODVOJIT ODDS, GAME_IDS I OSNOVNU PONUDU U ZASEBNE LISTE
        List<BigDecimal> oddsOnly = new ArrayList<>(); // radin listu BigDecimal oddova koja je prazna i u nju cu dodavat oddove
        List<Long> gameIds = new ArrayList<>(); // radin listu Long game_idova koja je prazna i u nju cu dodavat game_idove
        List<String> osnovnePonude = new ArrayList<>();
        for (String oddAndId : selectedOddsAndIds) { // loopan kroz svaki par gameId:selectedOdd
            String[] parts = oddAndId.split(":"); // splitan po : u array
            String gameId = parts[0]; // dohvacan indexiranjem game_id koji je na indexu 0
            String odd = parts[1]; // dohvacan indexiranjem odd koji je na indexu 1
            String tempOsnovnaPonuda = parts[2];
            oddsOnly.add(new BigDecimal(odd)); // dodajen u oddsOnly listu i pretavaran iz stringa u BigDecimal
            gameIds.add(Long.valueOf(gameId)); // dodajen u gameIds listu i pretvaran iz stringa u Long
            osnovnePonude.add(tempOsnovnaPonuda); // dodajen u osnovne_ponude listu i pretvaran
        }
        System.out.println(gameIds);

        BigDecimal amount = betDTO.getAmount();

        if (theUser.getBalance().compareTo(amount) < 0){
            throw new NotEnoughFundsException(theUser.getBalance());
        }

        BigDecimal totalOdd = new BigDecimal("1");
        for (BigDecimal odd : oddsOnly) {
            totalOdd = totalOdd.multiply(odd);
        }

        BigDecimal theWinings = totalOdd.multiply(amount);

        BigDecimal taxRate = new BigDecimal("0");

        if (theWinings.compareTo(MIN_TAX_BASE) <= 0) { // If the calling BigDecimal is numerically less than the argument, compareTo returns a negative value., If the calling BigDecimal is numerically equal to the argument, compareTo returns zero., If the calling BigDecimal is numerically greater than the argument, compareTo returns a positive value
            taxRate = new BigDecimal("0.1");
        } else if (theWinings.compareTo(MIN_TAX_BASE) > 0 && theWinings.compareTo(MEDIAN_TAX_BASE) <= 0) {
            taxRate = new BigDecimal("0.15");
        } else if (theWinings.compareTo(MEDIAN_TAX_BASE) > 0 && theWinings.compareTo(MAX_TAX_BASE) <= 0) {
            taxRate = new BigDecimal("0.20");
        } else if (theWinings.compareTo(MAX_TAX_BASE) > 0) {
            taxRate = new BigDecimal("0.25");
        }

        BigDecimal tax = theWinings.multiply(taxRate);

        BigDecimal finalPayment = theWinings.subtract(tax);

        // dohvati userov balance i na taj balance oduzmi amount koji uplacujemo za tiket
        BigDecimal totalBalance = theUser.getBalance().subtract(amount);

        // stavi useru taj novi balance
        theUser.setBalance(totalBalance);

        // postavi ostale fieldove
        theBet.setTotalBettingOdds(totalOdd);
        theBet.setWinnings(theWinings);
        theBet.setTax(tax);
        theBet.setPayment(finalPayment);
        //theBet.setUser(theUser);  // povezat bet sa useron, bet dobiva  foregign key user_id koji se odnosi na primarni key od usera
        theBet.setAmount(amount);

        //dodat bet objektu gameForBet objekte
        for (int i=0;i<gameIds.size();i++){ // gameIds, oddsOnly i osnovnePonud imaju isti size pa je svejedno sta cu stavit
            int index = i; // pomocna varijabla zbog toga sta lambda expression mora dohvacat lokale varijable a ne counter iz for petlje
            Game game = gameRepository.findById(gameIds.get(i)).orElseThrow(() -> new GameNotFoundException(gameIds.get(index))); // dohvatit game objekt po idu a id dobijemo loopanjen kroz listu
            BigDecimal selectedOdd = oddsOnly.get(i); // dohvatit single selected odd iz liste, taj selected odd odgovara ovom gori game objektu jer san ih zajedno dobija
            String osnovnaPonuda = osnovnePonude.get(i);
            GameForBet gameForBet = new GameForBet(game.getHomeTeam(), game.getAwayTeam(), selectedOdd, osnovnaPonuda);
            theBet.addGameForBet(gameForBet);
        }

        theUser.addBet(theBet); // uspostavit bi directional vezu

        betRepository.save(theBet); // spremit bet u bazu, spremit ce se i user i game for bet object zbog cascade

        return theBet; // STAVIJA SAN DA RETURNAN BET ZATO STA CU TAJ BET ONDA STAVIT U MODEL, KAD VRACAN OVAJ theBet imat ce popnjene fieldove jer je ovo gori odradeno unutar istog transactional contexta
    }

    @Override
    public List<Bet> findAllBetsByUserId(Long userId) {
        return betRepository.findByUserId(userId);
    }

    @Override
    public List<Bet> findAllBets() {
        return betRepository.findAll();
    }


    @Override
    public void changeBetResult() { // prominit cu status rezultata iz not finished u wining ili lost

        // dohvatit cu listu svih betova
        List<Bet> theBets = betRepository.findAll();

        // za svaki bet u toj listi cu stavit za result winiing ili lost pomocu metode koju san definira u pomocno
        for (Bet tempBet : theBets){
            if (tempBet.getResult() == BetResult.NOT_FINISHED){
                BetResult betResult = randomResultProvider.giveRandomResult();
                // update field od resulta sa random enumom
                tempBet.setResult(betResult);
                // spremit bet
                betRepository.save(tempBet);
            }
        }
    }

    @Override
    public void addPaymentToBalance(Bet theBet) {

        BigDecimal total = theBet.getUser().getBalance();

        BigDecimal payment = theBet.getPayment();

        BigDecimal editedTotal = total.add(payment);

        theBet.getUser().setBalance(editedTotal);

        betRepository.save(theBet); // sejvanjem beta spremamo i usera zbog cascadea sta san namistija. PODSJETNIK: So, in your case, because you're accessing the associated User object through the Bet object and updating its balance, the changes made to the User object's balance will be saved when you save the Bet object, due to the cascading behavior defined in your ORM mappings.
    }
}