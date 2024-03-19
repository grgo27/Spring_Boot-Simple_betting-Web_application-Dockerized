package com.vjezbanje.betting_app.DAO;

import com.vjezbanje.betting_app.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Long> {


}
