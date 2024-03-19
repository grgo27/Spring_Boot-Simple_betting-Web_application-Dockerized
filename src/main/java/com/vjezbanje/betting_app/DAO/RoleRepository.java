package com.vjezbanje.betting_app.DAO;

import com.vjezbanje.betting_app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    // NE MORAN STAVIT findFirst za vratit single rolu jer i ovo vraca prvu pronadenu rolu po ovom kriteriju
    // ako ocu listu roli onda stavljan findAllByName
    public Role findByName(String name);
}
