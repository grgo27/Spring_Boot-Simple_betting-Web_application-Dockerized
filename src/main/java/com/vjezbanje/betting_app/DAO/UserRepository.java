package com.vjezbanje.betting_app.DAO;

import com.vjezbanje.betting_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    // NE MORAN STAVIT findFirst za vratit single usera jer i ovo vraca prvog pronadenog usera po ovom kriteriju
    // ako ocu listu usera onda stavljan findAllByUsername
    public User findByUsername(String username);
}
