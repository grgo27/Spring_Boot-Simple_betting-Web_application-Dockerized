package com.vjezbanje.betting_app.DAO;

import com.vjezbanje.betting_app.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet,Long> {


    List<Bet> findByUserId(Long userId); // s obziron da san kod Bet entity definira i field User i stavija san da je
    // stupac za foreign key user_id, spring data automaski taj odnos razumi i pretopostalja da u tablici za bet postoji stupac user_id pa
    // ode mogu napravit onda ovakvu metodu
    // DRUGA STVAR STA TRIBAN OBRATIT POZORNOST JE DA GORI  IMENU NISAM STAVIJA FINDALL ALI NALAZI LISTU SVIHH JER SAN EDFINIRA DA VRACA LISTU. TO FINALL STAVLJAMO RADI ESTETSKIH RAZLOGA. BITNIJE JE STA STAVIMO ZA STA VRACA METODA. LIST<BET> ZACI DA CE VRATIT LISTU BET OBJEKATA. DA SAN STAVIJA SAMO BET RATILO BI PRVOG NADENOG BET OBJEKTA
}
