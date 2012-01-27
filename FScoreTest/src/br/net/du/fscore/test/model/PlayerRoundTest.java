package br.net.du.fscore.test.model;

import junit.framework.TestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;

public class PlayerRoundTest extends TestCase {
	private PlayerRound playerRound1;
	private PlayerRound playerRound2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		playerRound1 = new PlayerRound(new Player("Player One"));
		playerRound1.setBet(7);
		playerRound1.setWins(4);

		playerRound2 = new PlayerRound(new Player("Player One"));
		playerRound2.setBet(7);
		playerRound2.setWins(4);
	}

	public void testEquals() {
		assertEquals(playerRound1, playerRound2);
	}

	public void testHashCode() {
		assertEquals(playerRound1.hashCode(), playerRound2.hashCode());
	}

	public void testEqualsForSameObject() {
		assertEquals(playerRound1, playerRound1);
	}

	public void testHashCodeForSameObject() {
		assertEquals(playerRound1.hashCode(), playerRound1.hashCode());
	}

	public void testDifferentPlayersImpliesDifferentRound() {
		playerRound2.setPlayer(new Player("Player Two"));
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testDifferentBetsImpliesDifferentRound() {
		playerRound2.setBet(9);
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testDifferentWinsImpliesDifferentRound() {
		playerRound2.setWins(5);
		assertFalse(playerRound1.equals(playerRound2));
	}
}
