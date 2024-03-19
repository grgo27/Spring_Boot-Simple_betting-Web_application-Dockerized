package com.vjezbanje.betting_app.pomocno;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomResult implements RandomResultProvider {

    private static Random random = new Random();
    private static BetResult[] choices = {BetResult.LOST, BetResult.WINNING};

    public BetResult giveRandomResult() {

        int randomIndex = random.nextInt(choices.length);  // .length je kod arraya a od listi je .size()

        BetResult randomResult; // ovo je isto sta i BetResult randomResult = null;

        // malo vjezban swittch da ne zaboravin
        switch (randomIndex) {
            case 0:
                randomResult = BetResult.LOST;
                break;
            case 1:
                randomResult = BetResult.WINNING;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + randomIndex); // stavija san da za default izbaci ovaj exception
        }

        return randomResult;
    }
}

