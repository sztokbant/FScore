package br.net.du.fscore.model;

import java.util.Calendar;
import java.util.List;

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

	public void testDifferentPlayersImpliesDifference() {
		String name = "Same Name";
		Match aMatch = new Match(name);
		Match anotherMatch = new Match(name);
		aMatch.with(new Player(name));
		anotherMatch.with(new Player(name));

		assertEquals(aMatch, anotherMatch);
		aMatch.with(new Player("Another Player"));
		assertFalse(aMatch.equals(anotherMatch));
	}

	public void testDifferentRoundsImpliesDifference() {
		assertEquals(match1, match2);
		match2.addRound(round1);
		assertFalse(match1.equals(match2));
	}

	public void testPlayerCannotBeNull() {
		try {
			match1.with(null);
			fail("Player should not be null");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testCannotAddPlayersAfterMatchHasStarted() {
		Match match = new Match("Match");
		match.with(new Player("Player 1")).with(new Player("Player 2"));

		match.addRound(new Round(3));

		try {
			match.with(new Player("Player 3"));
			fail("Should have thrown an Exception");
		} catch (IllegalStateException e) {
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
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testNewRound() {
		Match match = new Match("Match");

		try {
			match.newRound(7);
			fail("should have thrown an Exception");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}

		match.with(new Player("A Player")).with(new Player("Another"));

		try {
			match.newRound(0);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		match.newRound(7);
		Round round = match.getRounds().get(0);
		assertEquals(2, round.getPlayerRounds().size());
		assertEquals(new Player("A Player"), round.getPlayerRounds().get(0)
				.getPlayer());
		assertEquals(new Player("Another"), round.getPlayerRounds().get(1)
				.getPlayer());
		assertEquals(match.getId(), round.getMatchId());
	}

	public void testCannotAddRoundIfMatchHasLessThan2Players() {
		match1.getRounds().remove(0);
		match1.deletePlayer(player1);
		try {
			match1.addRound(round1);
			fail("should not be able to add round when less than 2 players");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	public void testDeletePlayer() {
		try {
			match1.deletePlayer(player1);
			fail("should not delete after match has started");
		} catch (IllegalStateException e) {
			assertTrue(true);
		}

		match1.getRounds().remove(round1);

		assertEquals(2, match1.getPlayers().size());

		try {
			// delete successful
			assertEquals(true, match1.deletePlayer(player1));
			// delete failed, object not in list anymore
			assertEquals(false, match1.deletePlayer(player1));
		} catch (IllegalStateException e) {
			fail("deleting should be allowed at this point");
		}

		assertEquals(1, match1.getPlayers().size());
	}

	public void testNewRoundMaxCards() {
		Match match = new Match("Match");

		Player player1 = new Player("1");
		Player player2 = new Player("2");
		Player player3 = new Player("3");
		Player player4 = new Player("4");

		match.with(player1).with(player2);
		assertEquals(25, match.getMaxCardsPerRound());

		match.with(player3);
		assertEquals(17, match.getMaxCardsPerRound());

		match.with(player4);
		assertEquals(12, match.getMaxCardsPerRound());

		try {
			match.newRound(match.getMaxCardsPerRound() + 1);
			fail("should have thrown an Exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		match.newRound(match.getMaxCardsPerRound());
	}

	public void testNumberOfCardsSuggestion() {
		Match match = new Match("Match");

		Player player1 = new Player("1");
		Player player2 = new Player("2");
		Player player3 = new Player("3");
		Player player4 = new Player("4");

		match.with(player1).with(player2).with(player3).with(player4);

		long suggestion = match.getNumberOfCardsSuggestion();
		assertEquals(1, suggestion);

		for (long i = 1; i < match.getMaxCardsPerRound(); i++) {
			match.newRound(i);
			suggestion = match.getNumberOfCardsSuggestion();
			assertEquals(i + 1, suggestion);
		}

		for (long i = match.getMaxCardsPerRound() - 1; i > 1; i--) {
			match.newRound(i);
			suggestion = match.getNumberOfCardsSuggestion();
			assertEquals(i - 1, suggestion);
		}

		match.newRound(1);
		assertEquals(1, match.getNumberOfCardsSuggestion());
		match.newRound(1);
		assertEquals(1, match.getNumberOfCardsSuggestion());
		match.newRound(1);
		assertEquals(1, match.getNumberOfCardsSuggestion());
	}

	public void testGetScores() {
		Match match = new Match("Match");

		Player player1 = new Player("1");
		Player player2 = new Player("2");
		Player player3 = new Player("3");
		match.with(player1).with(player2).with(player3);

		Round round1 = new Round(7);
		PlayerRound pr1_1 = new PlayerRound(player1);
		pr1_1.setBet(4).setWins(5); // 5
		round1.addPlayerRound(pr1_1);
		PlayerRound pr1_2 = new PlayerRound(player2);
		pr1_2.setBet(2).setWins(2); // 7
		round1.addPlayerRound(pr1_2);
		PlayerRound pr1_3 = new PlayerRound(player3);
		pr1_3.setBet(7).setWins(0); // 0
		round1.addPlayerRound(pr1_3);

		Round round2 = new Round(8);
		PlayerRound pr2_1 = new PlayerRound(player1);
		pr2_1.setBet(8).setWins(3); // 5+3 = 8
		round2.addPlayerRound(pr2_1);
		PlayerRound pr2_2 = new PlayerRound(player2);
		pr2_2.setBet(0).setWins(2); // 7+2 = 9
		round2.addPlayerRound(pr2_2);
		PlayerRound pr2_3 = new PlayerRound(player3);
		pr2_3.setBet(0).setWins(0); // 0+5 = 5
		round2.addPlayerRound(pr2_3);

		Round round3 = new Round(9);
		PlayerRound pr3_1 = new PlayerRound(player1);
		pr3_1.setBet(5).setWins(5); // 8+10 = 18
		round3.addPlayerRound(pr3_1);
		PlayerRound pr3_2 = new PlayerRound(player2);
		pr3_2.setBet(6).setWins(1); // 9+1 = 10
		round3.addPlayerRound(pr3_2);
		PlayerRound pr3_3 = new PlayerRound(player3);
		pr3_3.setBet(3).setWins(3); // 5+8 = 13
		round3.addPlayerRound(pr3_3);

		match.addRound(round1).addRound(round2).addRound(round3);

		List<PlayerScore> scores = match.getPlayerScores();

		assertEquals(3, scores.size());
		assertEquals(18, scores.get(0).getScore());
		assertEquals(10, scores.get(1).getScore());
		assertEquals(13, scores.get(2).getScore());
	}
}
