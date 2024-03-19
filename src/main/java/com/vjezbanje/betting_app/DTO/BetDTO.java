package com.vjezbanje.betting_app.DTO;

import java.math.BigDecimal;
import java.util.List;

public class BetDTO {

    private BigDecimal amount;

    private List<String> selectedOddsAndIds; // Lista stringova, a string ce cinit kompinacija gameId, izabranog odda i osnovne ponude. ovako ce izgledat gameId:selectedOdd:1

    public BetDTO(){

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<String> getSelectedOddsAndIds() {
        return selectedOddsAndIds;
    }

    public void setSelectedOddsAndIds(List<String> selectedOddsAndIds) {
        this.selectedOddsAndIds = selectedOddsAndIds;
    }
}
