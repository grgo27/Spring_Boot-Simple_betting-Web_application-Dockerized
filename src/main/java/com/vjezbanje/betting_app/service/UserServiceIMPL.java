package com.vjezbanje.betting_app.service;

import com.vjezbanje.betting_app.DAO.RoleRepository;
import com.vjezbanje.betting_app.DAO.UserRepository;
import com.vjezbanje.betting_app.DTO.UpdateUser;
import com.vjezbanje.betting_app.DTO.WebUser;
import com.vjezbanje.betting_app.entity.Role;
import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.exception.NotEnoughFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class UserServiceIMPL implements UserService {

    // FIELDS/DEPENDECIES
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    // CONSTRUCTOR

    @Autowired
    public UserServiceIMPL(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void save(WebUser webUser) { // pri overridanju ove funkciju unutra mozemo radit sta ocemo
        // kreirat instancu user objekta
        User theUser = new User();

        // ispunit userove atribute pomocu webusera koji sadrzi podatke iz formi
        theUser.setUsername(webUser.getUsername());
        theUser.setPassword(passwordEncoder.encode(webUser.getPassword()));
        theUser.setEnabled(true);
        theUser.setFirstName(webUser.getFirstName());
        theUser.setLastName(webUser.getLastName());
        theUser.setEmail(webUser.getEmail());

        // dat useru defaultnu rolu USER tako sta cu trazit ROLE_EMPLOYEE iz baze i stavit u listu jer san stavija da field roles uzima Collekciju kao parametar
        theUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        // spremit usera (NE WEB USERA!!!!) u bazu
        userRepository.save(theUser); // USER REPOSITORY UZ OVU FINDBYUSERNAME METHODU ST ASMO DODATNO DEFINIRALI IMA I SVOJE DEFAULTNE findAll, findById, deleteById i save metode. i te sve metode mozemo korist kad koristimo Jparepository
    }

    @Override
    public void updateUser(UpdateUser updateUser) {
        User fetchedUser = userRepository.findByUsername(updateUser.getUsername());
        if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()){
            fetchedUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        if (updateUser.getFirstName() !=null){
            fetchedUser.setFirstName(updateUser.getFirstName());
        }
        if (updateUser.getLastName() !=null){
            fetchedUser.setLastName(updateUser.getLastName());
        }
        if (updateUser.getEmail() !=null){
            fetchedUser.setEmail(updateUser.getEmail());
        }

        userRepository.save(fetchedUser);
    }

    @Override
    public void depositMoney(String theUserName, BigDecimal amount) {

        // dohvati usera po username
        User fetchedUser = userRepository.findByUsername(theUserName);

        // dohvati userov balance i na taj balance dodaj novi amount
        BigDecimal total = fetchedUser.getBalance().add(amount);

        // stavi useru taj novi balance
        fetchedUser.setBalance(total);

        // spremi usera
        userRepository.save(fetchedUser);
    }

    @Override
    public void withdrawMoney(String theUserName, BigDecimal amount) {
        // dohvati usera po username
        User fetchedUser = userRepository.findByUsername(theUserName);

        // ako je userov balance manji od amounta koji zelimo podic onda baci exception
        if (fetchedUser.getBalance().compareTo(amount) < 0){
            throw new NotEnoughFundsException(fetchedUser.getBalance());
        }

        // dohvati userov balance i na taj balance dodaj novi amount
        BigDecimal total = fetchedUser.getBalance().subtract(amount);

        // stavi useru taj novi balance
        fetchedUser.setBalance(total);

        // spremi usera
        userRepository.save(fetchedUser);
    }

    @Override
    public UpdateUser createUpdateUser(User theUser) {
        UpdateUser updateUser = new UpdateUser();

        UpdateUser theUpdateUser = new UpdateUser();
        theUpdateUser.setUsername(theUser.getUsername());
        theUpdateUser.setPassword(theUser.getPassword());
        theUpdateUser.setFirstName(theUser.getFirstName());
        theUpdateUser.setLastName(theUser.getLastName());
        theUpdateUser.setEmail(theUser.getEmail());
        theUpdateUser.setBalance(theUser.getBalance());

        return theUpdateUser;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // argument za ovaj username je ono sta unesemo za username kada se pokusamo logirat
        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles()); //  This line maps the roles associated with the user to Spring Security SimpleGrantedAuthority objects. It calls the mapRolesToAuthorities method to convert the Role entities associated with the user into authorities.

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities); // this line constructs and returns a UserDetails object representing the authenticated user. It takes the username, password, and authorities as parameters. The UserDetails object is a core interface in Spring Security representing a user's details during authentication and authorization processes
    }

    // pomocna funkcija kojom konvertan kolekciju role objekata u kolekciju SimpleGrantedAuthoritja
    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) { //  This method converts a collection of Role entities into a collection of SimpleGrantedAuthority objects. This is necessary because Spring Security requires authorities to be granted to users during authentication and authorization processes.
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName()); // za konstruiranje SimleGrantedAuthority objekta potreban name je samo string (ime) role. i to ime dohvacamo tako da na role objektu pozovemo getName
            authorities.add(tempAuthority);
        }

        return authorities;
    }
}
