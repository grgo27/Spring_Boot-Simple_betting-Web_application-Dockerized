package com.vjezbanje.betting_app;

import com.vjezbanje.betting_app.DAO.RoleRepository;
import com.vjezbanje.betting_app.DAO.UserRepository;
import com.vjezbanje.betting_app.DTO.UpdateUser;
import com.vjezbanje.betting_app.DTO.WebUser;
import com.vjezbanje.betting_app.entity.Role;
import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.exception.NotEnoughFundsException;
import com.vjezbanje.betting_app.service.UserServiceIMPL;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceIMPL userService;

    @Test
    void testSave() {
        WebUser webUser = new WebUser();
        webUser.setUsername("testuser");
        webUser.setPassword("testpassword");
        webUser.setFirstName("John");
        webUser.setLastName("Doe");
        webUser.setEmail("john.doe@example.com");

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(roleUser);
        when(passwordEncoder.encode("testpassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.save(webUser);

        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(passwordEncoder, times(1)).encode("testpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setUsername("testuser");
        updateUser.setPassword("newpassword");
        updateUser.setFirstName("UpdatedFirstName");
        updateUser.setLastName("UpdatedLastName");
        updateUser.setEmail("updated.email@email.com");

        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setPassword("oldpassword");
        existingUser.setFirstName("OldFirstName");
        existingUser.setLastName("OldLastName");
        existingUser.setEmail("old.email@email.com");

        when(userRepository.findByUsername("testuser")).thenReturn(existingUser);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        userService.updateUser(updateUser);

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(existingUser);

        assert(existingUser.getPassword()).equals("encodedNewPassword");
        assert(existingUser.getFirstName()).equals("UpdatedFirstName");
        assert(existingUser.getLastName()).equals("UpdatedLastName");
        assert(existingUser.getEmail()).equals("updated.email@email.com");
    }

    @Test
    void testDepositMoney() {

        String username = "testuser";
        BigDecimal initialBalance = new BigDecimal("100");
        BigDecimal depositAmount = new BigDecimal("50");

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setBalance(initialBalance);

        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        userService.depositMoney(username, depositAmount);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(existingUser);

        assertEquals(initialBalance.add(depositAmount), existingUser.getBalance());
    }

    @Test
    void testWithdrawMoneySufficientFunds() {

        String username = "testuser";
        BigDecimal initialBalance = new BigDecimal("100");
        BigDecimal withdrawalAmount = new BigDecimal("50");

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setBalance(initialBalance);

        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        userService.withdrawMoney(username, withdrawalAmount);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(existingUser);

        assertEquals(initialBalance.subtract(withdrawalAmount), existingUser.getBalance());
    }

    @Test
    void testWithdrawMoneyInsufficientFunds() {

        String username = "testuser";
        BigDecimal initialBalance = new BigDecimal("50");
        BigDecimal withdrawalAmount = new BigDecimal("100");

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setBalance(initialBalance);

        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        assertThrows(NotEnoughFundsException.class, () -> userService.withdrawMoney(username, withdrawalAmount));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(existingUser);

        assertEquals(initialBalance, existingUser.getBalance());
    }

}
