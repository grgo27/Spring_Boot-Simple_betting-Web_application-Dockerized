package com.vjezbanje.betting_app.exception;

import java.text.MessageFormat;

public class GameNotFoundException extends RuntimeException{

    public GameNotFoundException(Long id){
        super(MessageFormat.format("Game with id: {0} is not found", id));
    }
}
