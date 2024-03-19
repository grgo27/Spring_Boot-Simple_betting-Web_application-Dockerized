package com.vjezbanje.betting_app;

import com.vjezbanje.betting_app.entity.Game;
import com.vjezbanje.betting_app.entity.Role;
import com.vjezbanje.betting_app.service.GameService;
import com.vjezbanje.betting_app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class SimpleBettingApp implements CommandLineRunner {

	@Autowired
	private RoleService roleService;

	@Autowired
	private GameService gameService;

	public static void main(String[] args) {
		SpringApplication.run(SimpleBettingApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if (roleService.countRoles() == 0){

			Role role = new Role("ROLE_USER");
			roleService.saveRole(role);
		}

		if (gameService.countGames() == 0){

			Game game1 = new Game("Slaven Belupo","Lokomotiva Zagreb",
					new BigDecimal("2.80"),
					new BigDecimal("3.20"),
					new BigDecimal("2.50"),
					new BigDecimal("1.50"),
					new BigDecimal("1.40"));

			Game game2 = new Game("Hajduk","Istra 1961",
					new BigDecimal("1.35"),
					new BigDecimal("5.50"),
					new BigDecimal("13.00"),
					new BigDecimal("1.05"),
					new BigDecimal("4.00"));

			Game game3 = new Game("Rijeka","Varaždin",
					new BigDecimal("1.25"),
					new BigDecimal("5.50"),
					new BigDecimal("11.00"),
					new BigDecimal("1.02"),
					new BigDecimal("3.60"));

			Game game4 = new Game("Osijek","Dinamo Zagreb",
					new BigDecimal("4.80"),
					new BigDecimal("3.50"),
					new BigDecimal("1.80"),
					new BigDecimal("2.00"),
					new BigDecimal("1.20"));

			Game game5 = new Game("Rudeš","HNK Gorica",
					new BigDecimal("4.10"),
					new BigDecimal("3.30"),
					new BigDecimal("1.90"),
					new BigDecimal("1.85"),
					new BigDecimal("1.20"));

			gameService.saveGame(game1);
			gameService.saveGame(game2);
			gameService.saveGame(game3);
			gameService.saveGame(game4);
			gameService.saveGame(game5);
		}


	}
}
