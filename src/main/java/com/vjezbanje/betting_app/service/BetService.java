package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.DTO.BetDTO;
import com.vjezbanje.betting_app.entity.Bet;

import java.util.List;

public interface BetService {

    Bet processBetDTO(BetDTO betDTO, String username);

    List<Bet> findAllBetsByUserId(Long userId);

    List<Bet> findAllBets();

    void changeBetResult();

    void addPaymentToBalance(Bet theBet);
}
