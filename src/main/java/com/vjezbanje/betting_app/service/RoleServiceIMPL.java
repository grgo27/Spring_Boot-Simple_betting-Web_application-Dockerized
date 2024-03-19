package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.DAO.RoleRepository;
import com.vjezbanje.betting_app.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceIMPL implements RoleService{

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceIMPL(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public long countRoles() {
        return roleRepository.count();
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }
}
