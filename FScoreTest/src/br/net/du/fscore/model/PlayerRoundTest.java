package br.net.du.fscore.model;

import br.net.du.fscore.model.exceptions.FScoreException;
import junit.framework.TestCase;

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

	public void testDifferentIdsImpliesDifference() {
		assertEquals(playerRound1, playerRound2);
		playerRound1.setId(playerRound2.getId() + 31);
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testDifferentRoundIdsImpliesDifference() {
		assertEquals(playerRound1, playerRound2);
		playerRound1.setRoundId(playerRound2.getRoundId() + 31);
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testDifferentPlayersImpliesDifference() throws FScoreException {
		assertEquals(playerRound1, playerRound2);
		playerRound2.setPlayer(new Player("Player Two"));
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testDifferentBetsImpliesDifference() {
		assertEquals(playerRound1, playerRound2);
		playerRound2.setBet(9);
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testDifferentWinsImpliesDifference() {
		assertEquals(playerRound1, playerRound2);
		playerRound2.setWins(5);
		assertFalse(playerRound1.equals(playerRound2));
	}

	public void testGetScore() throws FScoreException {
		// new instance
		PlayerRound playerRound = new PlayerRound(new Player("Name"));
		assertEquals(0, playerRound.getScore());

		// bet == wins
		playerRound.setBet(7);
		playerRound.setWins(7);
		assertEquals(7 + 5, playerRound.getScore());

		// bet != wins
		playerRound.setBet(3);
		playerRound.setWins(2);
		assertEquals(2, playerRound.getScore());
	}
}
