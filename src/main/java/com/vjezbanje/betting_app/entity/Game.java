package com.vjezbanje.betting_app.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "home_team")
    private String homeTeam;

    @Column(name = "away_team")
    private String awayTeam;

    @Column(name = "home_team_win_odds")
    private BigDecimal homeTeamWinOdds;

    @Column(name = "away_team_win_odds")
    private BigDecimal awayTeamWinOdds;

    @Column(name = "draw_odds")
    private BigDecimal drawOdds;

    @Column(name = "home_team_win_draw_odds")
    private BigDecimal homeTeamWinDrawOdds;

    @Column(name = "away_team_win_draw_odds")
    private BigDecimal awayTeamWinDrawOdds;

    public Game(){

    }

    public Game(String homeTeam, String awayTeam, BigDecimal homeTeamWinOdds, BigDecimal drawOdds,BigDecimal awayTeamWinOdds,  BigDecimal homeTeamWinDrawOdds, BigDecimal awayTeamWinDrawOdds) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamWinOdds = homeTeamWinOdds;
        this.drawOdds = drawOdds;
        this.awayTeamWinOdds = awayTeamWinOdds;
        this.homeTeamWinDrawOdds = homeTeamWinDrawOdds;
        this.awayTeamWinDrawOdds = awayTeamWinDrawOdds;
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

    public BigDecimal getHomeTeamWinOdds() {
        return homeTeamWinOdds;
    }

    public void setHomeTeamWinOdds(BigDecimal homeTeamWinOdds) {
        this.homeTeamWinOdds = homeTeamWinOdds;
    }

    public BigDecimal getAwayTeamWinOdds() {
        return awayTeamWinOdds;
    }

    public void setAwayTeamWinOdds(BigDecimal awayTeamWinOdds) {
        this.awayTeamWinOdds = awayTeamWinOdds;
    }

    public BigDecimal getDrawOdds() {
        return drawOdds;
    }

    public void setDrawOdds(BigDecimal drawOdds) {
        this.drawOdds = drawOdds;
    }

    public BigDecimal getHomeTeamWinDrawOdds() {
        return homeTeamWinDrawOdds;
    }

    public void setHomeTeamWinDrawOdds(BigDecimal homeTeamWinDrawOdds) {
        this.homeTeamWinDrawOdds = homeTeamWinDrawOdds;
    }

    public BigDecimal getAwayTeamWinDrawOdds() {
        return awayTeamWinDrawOdds;
    }

    public void setAwayTeamWinDrawOdds(BigDecimal awayTeamWinDrawOdds) {
        this.awayTeamWinDrawOdds = awayTeamWinDrawOdds;
    }


}
