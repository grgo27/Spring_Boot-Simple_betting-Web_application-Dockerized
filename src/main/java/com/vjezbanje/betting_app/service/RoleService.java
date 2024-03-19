package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.entity.Role;

public interface RoleService {

    long countRoles();

    void saveRole(Role role);
}
