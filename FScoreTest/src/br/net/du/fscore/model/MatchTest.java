package br.net.du.fscore.model;

import java.util.Calendar;
import java.util.List;

import br.net.du.fscore.model.exceptions.FScoreException;

import android.test.AndroidTestCase;

public class MatchTest extends AndroidTestCase {

	private Round round1;
	private Round round2;

	private PlayerRound playerRound1;
	private PlayerRound playerRound2;

	private Player player1;
	private Player player2;

	private Match match1;
	private Match match2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// creating 2 equal matches

		player1 = new Player("Player Name");
		player2 = new Player("Another Player Name");

		playerRound1 = new PlayerRound(player1).setBet(7).setWins(4);
		playerRound2 = new PlayerRound(player2).setBet(7).setWins(4);

		round1 = new Round(7).addPlayerRound(playerRound1).addPlayerRound(
				playerRound2);
		round2 = new Round(7).addPlayerRound(playerRound1).addPlayerRound(
				playerRound2);

		Calendar date = Calendar.getInstance();

		match1 = new Match();
		match1.setName("Super Match");
		match1.setDate(date);
		match1.with(player1).with(player2);
		match1.addRound(round1);

		match2 = new Match();
		match2.setName("Super Match");
		match2.setDate(date);
		match2.with(player1).with(player2);
		match2.addRound(round2);
	}

	public void testEquals() {
		assertEquals(match1, match2);
	}

	public void testEqualsForSameObject() {
		assertEquals(match1, match1);
	}

	public void testHashCode() {
		assertEquals(match1.hashCode(), match2.hashCode());
	}

	public void testHashCodeForSameObject() {
		assertEquals(match1.hashCode(), match1.hashCode());
	}

	public void testDifferentNamesImpliesDifference() {
		assertEquals(match1, match2);
		match2.setName("Another Name");
		assertFalse(match1.equals(match2));
	}

	public void testDifferentIdsImpliesDifference() {
		assertEquals(match1, match2);
		match2.setId(match1.getId() + 31);
		assertFalse(match1.equals(match2));
	}

	public void testDifferentDatesImpliesDifference() {
		assertEquals(match1, match2);
		Calendar nextYear = Calendar.getInstance();
		nextYear.roll(Calendar.YEAR, 1);
		match2.setDate(nextYear);
		assertFalse(match1.equals(match2));
	}

	public void testSameDatesDifferentObjectsImpliesEquivalence() {
		Calendar otherCalendar = Calendar.getInstance();
		otherCalendar.setTimeInMillis(match1.getDate().getTimeInMillis());
		match2.setDate(otherCalendar);
		assertEquals(match1, match2);
	}

	public void testDifferentPlayersImpliesDifference() throws FScoreException {
		match1.rounds.clear();
		match2.rounds.clear();
		assertEquals(match1, match2);

		match1.with(new Player("Yet Another"));
		assertFalse(match1.equals(match2));
	}

	public void testDifferentRoundsImpliesDifference() throws FScoreException {
		assertEquals(match1, match2);
		match2.addRound(round1);
		assertFalse(match1.equals(match2));
	}

	public void testPlayerCannotBeNull() {
		match1.rounds.clear();

		try {
			match1.with(null);
			fail("Player should not be null");
		} catch (FScoreException e) {
			assertTrue(true);
		}
	}

	public void testCannotAddPlayersAfterMatchHasStarted() {
		try {
			match1.with(new Player("Player 3"));
			fail("Should have thrown an Exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}
	}

	public void testCannotAddMoreThan51Players() throws FScoreException {
		match1.getRounds().clear();
		match1.getPlayers().clear();

		for (int i = 0; i < 51; i++) {
			match1.with(new Player("Player " + String.valueOf(i)));
		}

		assertEquals(51, match1.getPlayers().size());

		try {
			match1.with(new Player("Yet Another"));
			fail("should have thrown an Exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}
	}

	public void testSettingMatchIdSetsRoundsIds() {
		long newMatchId = match1.getId() + 19;

		assertFalse(match1.getRounds().get(0).getMatchId() == newMatchId);

		match1.setId(newMatchId);

		assertEquals(newMatchId, match1.getRounds().get(0).getMatchId());
	}

	public void testRoundCannotBeNull() {
		try {
			match1.addRound(null);
			fail("Round should not be null");
		} catch (FScoreException e) {
			assertTrue(true);
		}
	}

	public void testNewRoundNoArgsShouldIntelligentlySetItsNumberOfCards()
			throws FScoreException {
		Match match = new Match("Match");

		Player player1 = new Player("1");
		Player player2 = new Player("2");
		Player player3 = new Player("3");

		match.with(player1).with(player2).with(player3);

		Round currentRound;

		// ascending loop
		for (int i = 0; i < 17; i++) {
			currentRound = match.newRound();

			assertEquals(i + 1, match.getRounds().size());
			assertEquals(i + 1, match.getRounds().get(i).getNumberOfCards());

			currentRound.setBet(player1, 0);
			currentRound.setBet(player2, 0);
			currentRound.setBet(player3, currentRound.getNumberOfCards() - 1);

			currentRound.setWins(player1, 0);
			currentRound.setWins(player2, 0);
			currentRound.setWins(player3, currentRound.getNumberOfCards());
		}

		// descending loop
		for (int i = 16, nRounds = 18; i > 0; i--, nRounds++) {
			currentRound = match.newRound();

			assertEquals(nRounds, match.getRounds().size());
			assertEquals(i + 1, match.getRounds().get(i).getNumberOfCards());

			currentRound.setBet(player1, 0);
			currentRound.setBet(player2, 0);
			currentRound.setBet(player3, currentRound.getNumberOfCards() - 1);

			currentRound.setWins(player1, 0);
			currentRound.setWins(player2, 0);
			currentRound.setWins(player3, currentRound.getNumberOfCards());
		}

		assertEquals(33, match.getRounds().size());
		assertEquals(1, match.getRounds().get(32).getNumberOfCards());

		try {
			match.newRound();
			fail("should have thrown an Exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}
	}

	public void testNewRound() throws FScoreException {
		Match match = new Match("Match");

		try {
			match.newRound(7);
			fail("should have thrown an Exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}

		match.with(new Player("A Player")).with(new Player("Another"));

		try {
			match.newRound(0);
			fail("should have thrown an Exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}

		Round round = match.newRound(7);
		assertEquals(2, round.getPlayerRounds().size());
		assertEquals(new Player("A Player"), round.getPlayerRounds().get(0)
				.getPlayer());
		assertEquals(new Player("Another"), round.getPlayerRounds().get(1)
				.getPlayer());
		assertEquals(match.getId(), round.getMatchId());
	}

	public void testCannotAddRoundIfMatchHasLessThan2Players()
			throws FScoreException {
		match1.getRounds().clear();
		match1.deletePlayer(player1);

		try {
			match1.addRound(round1);
			fail("should not be able to add round when less than 2 players");
		} catch (FScoreException e) {
			assertTrue(true);
		}

		match1.with(player1);
		match1.addRound(round1);
	}

	public void testDeletePlayer() {
		try {
			match1.deletePlayer(player1);
			fail("should not delete after match has started");
		} catch (FScoreException e) {
			assertTrue(true);
		}

		match1.getRounds().clear();

		assertEquals(2, match1.getPlayers().size());

		try {
			// delete successful
			assertEquals(true, match1.deletePlayer(player1));
			// delete failed, object not in list anymore
			assertEquals(false, match1.deletePlayer(player1));
		} catch (FScoreException e) {
			fail("deleting should be allowed at this point");
		}

		assertEquals(1, match1.getPlayers().size());
	}

	public void testNewRoundMaxCards() throws FScoreException {
		Match match = new Match("Match");

		match.with(new Player("1")).with(new Player("2"));
		assertEquals(25, match.getMaxCardsPerRound());

		match.with(new Player("3"));
		assertEquals(17, match.getMaxCardsPerRound());

		match.with(new Player("4"));
		assertEquals(12, match.getMaxCardsPerRound());

		try {
			match.newRound(match.getMaxCardsPerRound() + 1);
			fail("should have thrown an Exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}

		match.newRound(match.getMaxCardsPerRound());
	}

	public void testGetScores() throws FScoreException {
		match1.getRounds().clear();

		Player player3 = new Player("3");
		match1.with(player3);

		Round round1 = new Round(7);
		round1.addPlayerRound(new PlayerRound(player1).setBet(4).setWins(5)); // 5
		round1.addPlayerRound(new PlayerRound(player2).setBet(2).setWins(2)); // 7
		round1.addPlayerRound(new PlayerRound(player3).setBet(7).setWins(0)); // 0

		Round round2 = new Round(8);
		round2.addPlayerRound(new PlayerRound(player1).setBet(8).setWins(3)); // 3
		round2.addPlayerRound(new PlayerRound(player2).setBet(0).setWins(2)); // 2
		round2.addPlayerRound(new PlayerRound(player3).setBet(0).setWins(0)); // 5

		Round round3 = new Round(9);
		round3.addPlayerRound(new PlayerRound(player1).setBet(5).setWins(5)); // 10
		round3.addPlayerRound(new PlayerRound(player2).setBet(6).setWins(1)); // 1
		round3.addPlayerRound(new PlayerRound(player3).setBet(3).setWins(3)); // 8

		match1.addRound(round1).addRound(round2).addRound(round3);

		List<PlayerScore> scores = match1.getPlayerScores();

		assertEquals(3, scores.size());
		assertEquals(player1, scores.get(0).getPlayer());
		assertEquals(18, scores.get(0).getScore());
		assertEquals(player2, scores.get(1).getPlayer());
		assertEquals(10, scores.get(1).getScore());
		assertEquals(player3, scores.get(2).getPlayer());
		assertEquals(13, scores.get(2).getScore());
	}

	public void testCannotAddDuplicatedPlayers() {
		match1.getRounds().clear();

		try {
			match1.with(new Player(player1.getName()));
			fail("should have thrown an exception");
		} catch (FScoreException e) {
			assertTrue(true);
		}
	}
}
