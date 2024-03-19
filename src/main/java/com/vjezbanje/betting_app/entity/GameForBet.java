package com.vjezbanje.betting_app.entity;

import com.vjezbanje.betting_app.entity.Bet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games_for_bet")
public class GameForBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "home_team")
    private String homeTeam;

    @Column(name = "awayTeam")
    private String awayTeam;

    @Column(name = "selected_odd")
    private BigDecimal selectedOdd;

    @Column(name = "osnovna_ponuda")
    private String osnovnaPonuda;

    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "bet_game",
            joinColumns = @JoinColumn(name = "game_for_bet_id"),
            inverseJoinColumns = @JoinColumn(name = "bet_id")
    )
    private List<Bet> bets;

    public GameForBet() {
    }

    public GameForBet(String homeTeam, String awayTeam, BigDecimal selectedOdd, String osnovnaPonuda) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.selectedOdd = selectedOdd;
        this.osnovnaPonuda = osnovnaPonuda;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public BigDecimal getSelectedOdd() {
        return selectedOdd;
    }

    public void setSelectedOdd(BigDecimal selectedOdd) {
        this.selectedOdd = selectedOdd;
    }

    public String getOsnovnaPonuda() {
        return osnovnaPonuda;
    }

    public void setOsnovnaPonuda(String osnovnaPonuda) {
        this.osnovnaPonuda = osnovnaPonuda;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }

    // metoda za dodat bet
    public void addBet(Bet bet){
        if (bets == null){
            bets = new ArrayList<>();
        }
        bets.add(bet);
    }
}
