package com.vjezbanje.betting_app.entity;

import com.vjezbanje.betting_app.pomocno.BetResult;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bet")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "total_odds")
    private BigDecimal totalBettingOdds;

    @Column(name = "winnings")
    private BigDecimal winnings;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "result")
    @Enumerated(EnumType.STRING) // kada koristin objekt enum type za field moran stavit i @Enumerated anotaciju i odredit tip podatka koji ce bit u bazi. ode san stavija da ce to bit string
    private BetResult result = BetResult.NOT_FINISHED;

    @Column(name = "payment")
    private BigDecimal payment;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "bet_game",
            joinColumns = @JoinColumn(name = "bet_id"),
            inverseJoinColumns = @JoinColumn(name = "game_for_bet_id")
    )
    private List<GameForBet> gamesForBet;

    public Bet(){

    }
    public Bet(BigDecimal amount, BigDecimal totalBettingOdds, BigDecimal winnings, BigDecimal tax, BigDecimal payment, BetResult result) {
        this.amount = amount;
        this.totalBettingOdds = totalBettingOdds;
        this.winnings = winnings;
        this.tax = tax;
        this.payment = payment;
        this.result = result;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotalBettingOdds() {
        return totalBettingOdds;
    }

    public void setTotalBettingOdds(BigDecimal totalBettingOdds) {
        this.totalBettingOdds = totalBettingOdds;
    }

    public BigDecimal getWinnings() {
        return winnings;
    }

    public void setWinnings(BigDecimal winnings) {
        this.winnings = winnings;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BetResult getResult() {
        return result;
    }

    public void setResult(BetResult result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GameForBet> getGamesForBet() {
        return gamesForBet;
    }

    public void setGamesForBet(List<GameForBet> gamesForBet) {
        this.gamesForBet = gamesForBet;
    }

    // metoda za dodat game
    public void addGameForBet(GameForBet gameForBet){
        if (gamesForBet == null){
            gamesForBet = new ArrayList<>();
        }
        gamesForBet.add(gameForBet);
    }

    @Override
    public String toString() {
        return "Bet{" +
                "id=" + id +
                ", amount=" + amount +
                ", totalBettingOdds=" + totalBettingOdds +
                ", winnings=" + winnings +
                ", tax=" + tax +
                ", payment=" + payment +
                '}';
    }
}
