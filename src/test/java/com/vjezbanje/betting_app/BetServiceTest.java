package com.vjezbanje.betting_app;

import com.vjezbanje.betting_app.DAO.BetRepository;
import com.vjezbanje.betting_app.DAO.GameRepository;
import com.vjezbanje.betting_app.DAO.UserRepository;
import com.vjezbanje.betting_app.DTO.BetDTO;
import com.vjezbanje.betting_app.entity.Bet;
import com.vjezbanje.betting_app.entity.Game;
import com.vjezbanje.betting_app.entity.User;
import com.vjezbanje.betting_app.exception.GameNotFoundException;
import com.vjezbanje.betting_app.exception.NotEnoughFundsException;
import com.vjezbanje.betting_app.pomocno.BetResult;
import com.vjezbanje.betting_app.pomocno.RandomResultProvider;
import com.vjezbanje.betting_app.service.BetServiceIMPL;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@TestPropertySource("/application.properties")
@SpringBootTest
class BetServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BetRepository betRepository;

	@Mock
	private GameRepository gameRepository;

	@Mock
	private RandomResultProvider randomResultProvider;

	@InjectMocks
	private BetServiceIMPL betService;

	@Test
	void testFindAllBetsByUserId() {
		Long userId = 1L;

		List<Bet> expectedBets = Arrays.asList(
				new Bet(), new Bet(), new Bet()
		);

		// Mockat cu ponasanje  i response od betRepository.find...
		when(betRepository.findByUserId(userId)).thenReturn(expectedBets);

		// dohvatit cu actual
		List<Bet> actualBets = betService.findAllBetsByUserId(userId);

		// assertat
		assertEquals(expectedBets.size(), actualBets.size());

	}

	@Test
	void testFindAllBets() {

		List<Bet> expectedBets = Arrays.asList(
				new Bet(), new Bet(), new Bet()
		);

		//mockat ponasanje
		when(betRepository.findAll()).thenReturn(expectedBets);

		// dohvatit actual
		List<Bet> actualBets = betService.findAllBets();

		// assert
		assertEquals(expectedBets.size(), actualBets.size());
	}

	@Test
	void testChangeBetResult() {
		List<Bet> theBets = Arrays.asList(
				new Bet(), new Bet(), new Bet()
		);
		when(betRepository.findAll()).thenReturn(theBets);
		when(randomResultProvider.giveRandomResult()).thenReturn(BetResult.WINNING); // Stavit cu winning a mogu stavit i losing

		betService.changeBetResult();

		for (Bet bet : theBets) {
			verify(betRepository, times(1)).save(bet);
			assertEquals(BetResult.WINNING, bet.getResult());
		}
	}

	@Test
	void testAddPaymentToBalance() {
		// napravit setup, ne trian sve fieldove usera i beta
		User user = new User();
		user.setBalance(new BigDecimal("100"));
		Bet theBet = new Bet();
		theBet.setUser(user);
		theBet.setPayment(new BigDecimal("50"));

		// pozivan betService metodu. zbog toga sta san mocka betRepository ovo nece zapravo spremit u bazu
		betService.addPaymentToBalance(theBet);

		BigDecimal expectedBalance = new BigDecimal("150"); // ocekivani rezultat nakon dodavanja paymenta
		assertEquals(0,expectedBalance.compareTo(user.getBalance()) ); // radin assert
		verify(betRepository, times(1)).save(theBet); // verifyat cu da je betRepository.save pozvana jedanput
	}

	@Test
	void testProcessBetDTO() {

		User user = new User();
		user.setUsername("testUser");
		user.setBalance(new BigDecimal("100"));
		when(userRepository.findByUsername("testUser")).thenReturn(user);

		// ne tribaju mi svi fieldovi od game objekta, ispunit cu samo one koje koristin za apravit gameforbet objekt
		Game game1 = new Game();
		game1.setId(1L);
		game1.setHomeTeam("Home Team 1");
		game1.setAwayTeam("Away Team 1");

		Game game2 = new Game();
		game2.setId(2L);
		game2.setHomeTeam("Home Team 2");
		game2.setAwayTeam("Away Team 2");

		when(gameRepository.findById(1L)).thenReturn(Optional.of(game1));
		when(gameRepository.findById(2L)).thenReturn(Optional.of(game2));

		BetDTO betDTO = new BetDTO();
		betDTO.setAmount(new BigDecimal("10"));
		betDTO.setSelectedOddsAndIds(Arrays.asList("1:2:1", "2:1.5:2")); // gameId:selectedOdd:osnovnaPonuda

		Bet processedBet = betService.processBetDTO(betDTO, "testUser");

		assertNotNull(processedBet);
		assertEquals(0, new BigDecimal("30").compareTo(processedBet.getWinnings()));
		assertEquals(0,new BigDecimal("3").compareTo(processedBet.getTax()));
		assertEquals(0,new BigDecimal("27").compareTo(processedBet.getPayment()) );
		assertEquals(0,new BigDecimal("90").compareTo(user.getBalance()) );
		assertEquals(2, processedBet.getGamesForBet().size());
		verify(betRepository, times(1)).save(processedBet);
	}

	@Test
	void testProcessBetDTONotEnoughFunds() {
		User user = new User();
		user.setUsername("testUser");
		user.setBalance(new BigDecimal("5"));
		when(userRepository.findByUsername("testUser")).thenReturn(user);

		BetDTO betDTO = new BetDTO();
		betDTO.setAmount(new BigDecimal("10"));
		betDTO.setSelectedOddsAndIds(new ArrayList<>()); // moran i ovo inicijalizirat inace dobivan error. stavit cu ko praznu listu

		assertThrows(NotEnoughFundsException.class, () -> betService.processBetDTO(betDTO, "testUser"));
		verify(userRepository, never()).save(any(User.class));
		verify(betRepository, never()).save(any(Bet.class));
	}

	@Test
	void testProcessBetDTOGameNotFound() {
		User user = new User();
		user.setUsername("testUser");
		user.setBalance(new BigDecimal("100"));
		when(userRepository.findByUsername("testUser")).thenReturn(user);

		BetDTO betDTO = new BetDTO();
		betDTO.setAmount(new BigDecimal("10"));
		betDTO.setSelectedOddsAndIds(Arrays.asList("1:2:1", "2:1.5:2"));

		when(gameRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(GameNotFoundException.class, () -> betService.processBetDTO(betDTO, "testUser"));
		verify(userRepository, never()).save(any(User.class));
		verify(betRepository, never()).save(any(Bet.class));
	}

}


