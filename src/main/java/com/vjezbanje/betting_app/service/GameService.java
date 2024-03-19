package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.entity.Game;

import java.util.List;

public interface GameService {

    List<Game> findAll();

    void deleteGames();

    long countGames();

    void saveGame(Game game);

}
