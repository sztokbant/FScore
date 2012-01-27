package br.net.du.fscore.test.model;

import java.util.Calendar;

import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;

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

		String name = "Player Name";
		player1 = new Player(name);
		player2 = new Player(name);

		round1 = new Round(7);
		round2 = new Round(7);

		playerRound1 = new PlayerRound(player1);
		playerRound1.setBet(7);
		playerRound1.setWins(4);

		playerRound2 = new PlayerRound(player2);
		playerRound2.setBet(7);
		playerRound2.setWins(4);

		round1.addPlayerRound(playerRound1);
		round2.addPlayerRound(playerRound2);

		Calendar date = Calendar.getInstance();

		match1 = new Match();
		match1.setName("Super Match");
		match1.setDate(date);
		match1.addRound(round1);
		match1.withPlayer(player1);

		match2 = new Match();
		match2.setName("Super Match");
		match2.setDate(date);
		match2.addRound(round2);
		match2.withPlayer(player2);
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

	public void testDifferentNamesImplyDifference() {
		match2.setName("Another Name");
		assertFalse(match1.equals(match2));
	}

	public void testDifferentDatesImplyDifference() {
		Calendar nextYear = Calendar.getInstance();
		nextYear.roll(Calendar.YEAR, 1);
		match2.setDate(nextYear);
		assertFalse(match1.equals(match2));
	}

	public void testDifferentPlayersImplyDifference() {
		match2.withPlayer(new Player("Yet Another Player"));
		assertFalse(match1.equals(match2));
	}

	public void testDifferentRoundsImplyDifference() {
		match2.addRound(round1);
		assertFalse(match1.equals(match2));
	}
}
