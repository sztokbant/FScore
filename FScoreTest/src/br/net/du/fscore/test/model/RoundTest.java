package br.net.du.fscore.test.model;

import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;

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

	public void testDifferentNumberOfCardsImplyDifference() {
		round1.setNumberOfCards(3);
		assertFalse(round1.equals(round2));
	}

	public void testDifferentSetOfPlayerRoundsImplyDifference() {
		round2.addPlayerRound(playerRound1);
		assertFalse(round1.equals(round2));
	}
}
