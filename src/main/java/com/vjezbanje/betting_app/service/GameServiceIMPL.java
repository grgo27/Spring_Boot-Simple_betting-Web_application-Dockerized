package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.DAO.GameRepository;
import com.vjezbanje.betting_app.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceIMPL implements GameService{

    private GameRepository gameRepository;

    @Autowired
    public GameServiceIMPL(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Override
    public void deleteGames() {

        List<Game> games = gameRepository.findAll();

        for (Game tempGame : games){

            Long theId = tempGame.getId();
            gameRepository.deleteById(theId);
        }

    }

    @Override
    public long countGames() {
        return gameRepository.count();
    }

    @Override
    public void saveGame(Game game) {
        gameRepository.save(game);
    }
}
