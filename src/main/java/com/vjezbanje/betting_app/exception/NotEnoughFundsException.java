package com.vjezbanje.betting_app.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class NotEnoughFundsException extends RuntimeException{

    public NotEnoughFundsException(BigDecimal funds){
        super(MessageFormat.format("Not enough funds. You have: {0}",funds));
    }
}
