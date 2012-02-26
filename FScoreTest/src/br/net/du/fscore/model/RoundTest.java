package br.net.du.fscore.model;

import android.test.AndroidTestCase;

public class RoundTest extends AndroidTestCase {

	private Round round1;
	private Round round2;

	private PlayerRound playerRound1;
	private PlayerRound playerRound2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		round1 = new Round(7);
		round2 = new Round(7);

		playerRound1 = new PlayerRound(new Player("Player One"));
		playerRound1.setBet(7);
		playerRound1.setWins(4);

		playerRound2 = new PlayerRound(new Player("Player One"));
		playerRound2.setBet(7);
		playerRound2.setWins(4);

		round1.addPlayerRound(playerRound1);
		round2.addPlayerRound(playerRound2);
	}

	public void testEquals() {
		assertEquals(round1, round2);
	}

	public void testHashCode() {
		assertEquals(round1.hashCode(), round2.hashCode());
	}

	public void testEqualsForSameObject() {
		assertEquals(round1, round1);
	}

	public void testHashCodeForSameObject() {
		assertEquals(round1.hashCode(), round1.hashCode());
	}

	public void testDifferentIdsImpliesDifference() {
		assertEquals(round1, round2);
		round1.setId(round2.getId() + 31);
		assertFalse(round1.equals(round2));
	}

	public void testDifferentMatchIdsImpliesDifference() {
		assertEquals(round1, round2);
		round1.setMatchId(round2.getMatchId() + 31);
		assertFalse(round1.equals(round2));
	}

	public void testDifferentNumberOfCardsImpliesDifference() {
		assertEquals(round1, round2);
		round1.setNumberOfCards(3);
		assertFalse(round1.equals(round2));
	}

	public void testDifferentSetOfPlayerRoundsImpliesDifference() {
		assertEquals(round1, round2);
		round2.addPlayerRound(new PlayerRound(new Player("Another")));
		assertFalse(round1.equals(round2));
	}

	public void testCannotAddMoreThanOnePlayerRoundForSamePlayer() {
		try {
			round1.addPlayerRound(new PlayerRound(new Player("Player One")));
			fail("should have thrown an Exception");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	public void testPlayerRoundCannotBeNull() {
		try {
			round1.addPlayerRound(null);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testSetBetForPlayer() {
		Round round = new Round(5);

		Player player1 = new Player("Player 1");
		Player player2 = new Player("Player 2");
		Player player3 = new Player("Player 3");
		Player player4 = new Player("Player 4");

		PlayerRound playerRound1 = new PlayerRound(player1);
		PlayerRound playerRound2 = new PlayerRound(player2);
		PlayerRound playerRound3 = new PlayerRound(player3);
		PlayerRound playerRound4 = new PlayerRound(player4);

		round.addPlayerRound(playerRound1).addPlayerRound(playerRound2)
				.addPlayerRound(playerRound3).addPlayerRound(playerRound4);

		round.setBet(player1, 0);
		round.setBet(player2, 2);
		round.setBet(player3, 3);

		try {
			round.setBet(player4, 9);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			round.setBet(player4, 0);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		round.setBet(player4, 2);
	}

	public void testSetWinsForPlayer() {
		Round round = new Round(5);

		Player player1 = new Player("Player 1");
		Player player2 = new Player("Player 2");

		PlayerRound playerRound1 = new PlayerRound(player1);
		PlayerRound playerRound2 = new PlayerRound(player2);

		round.addPlayerRound(playerRound1).addPlayerRound(playerRound2);

		try {
			round.setWins(player1, 2);
			fail("should have thrown an Exception");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}

		round.setBet(player1, 4);

		try {
			round.setWins(player1, 2);
			fail("should have thrown an Exception");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}

		round.setBet(player2, 2);
		round.setWins(player1, 2);

		try {
			round.setWins(player2, 9);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			round.setWins(player2, 2);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		round.setBet(player2, 3);
	}

	public void testNumberOfCardsMustBeGreaterThanZero() {
		Round round = null;
		try {
			round = new Round(0);
			fail("should have thrown an Exeption");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			round = new Round(-1);
			fail("should have thrown an Exeption");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		round = new Round(19);

		try {
			round.setNumberOfCards(0);
			fail("should have thrown an Exeption");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		try {
			round.setNumberOfCards(-1);
			fail("should have thrown an Exeption");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		round.setNumberOfCards(47);
		assertEquals(47, round.getNumberOfCards());
	}

	public void testGetForbiddenBet() {
		Player player1 = new Player("Player 1");
		Player player2 = new Player("Player 2");
		Player player3 = new Player("Player 3");

		PlayerRound playerRound1 = new PlayerRound(player1);
		PlayerRound playerRound2 = new PlayerRound(player2);
		PlayerRound playerRound3 = new PlayerRound(player3);

		Round round = new Round(3).addPlayerRound(playerRound1)
				.addPlayerRound(playerRound2).addPlayerRound(playerRound3);

		assertEquals(Round.NO_FORBIDDEN_BET, round.getForbiddenBet(player3));

		round.getPlayerRounds().get(0).setBet(1);
		assertEquals(Round.NO_FORBIDDEN_BET, round.getForbiddenBet(player3));

		round.getPlayerRounds().get(1).setBet(1);
		assertEquals(1, round.getForbiddenBet(player3));

		round.getPlayerRounds().get(2).setBet(2);
		assertEquals(0, round.getForbiddenBet(player2));
	}

	public void testIsComplete() {
		Player player1 = new Player("Player 1");
		Player player2 = new Player("Player 2");
		Player player3 = new Player("Player 3");

		PlayerRound playerRound1 = new PlayerRound(player1);
		PlayerRound playerRound2 = new PlayerRound(player2);
		PlayerRound playerRound3 = new PlayerRound(player3);

		Round round = new Round(3).addPlayerRound(playerRound1)
				.addPlayerRound(playerRound2).addPlayerRound(playerRound3);

		assertEquals(false, round.isComplete());

		playerRound1.setBet(0).setWins(1);
		assertEquals(false, round.isComplete());

		playerRound2.setBet(1).setWins(1);
		assertEquals(false, round.isComplete());

		playerRound3.setBet(3).setWins(1);
		assertEquals(true, round.isComplete());
	}

	public void testHasAllBets() {
		Player player1 = new Player("Player 1");
		Player player2 = new Player("Player 2");
		Player player3 = new Player("Player 3");

		PlayerRound playerRound1 = new PlayerRound(player1);
		PlayerRound playerRound2 = new PlayerRound(player2);
		PlayerRound playerRound3 = new PlayerRound(player3);

		Round round = new Round(3).addPlayerRound(playerRound1)
				.addPlayerRound(playerRound2).addPlayerRound(playerRound3);

		assertEquals(false, round.hasAllBets());

		playerRound1.setBet(0);
		assertEquals(false, round.hasAllBets());

		playerRound2.setBet(1);
		assertEquals(false, round.hasAllBets());

		playerRound3.setBet(3);
		assertEquals(true, round.hasAllBets());
	}
}
