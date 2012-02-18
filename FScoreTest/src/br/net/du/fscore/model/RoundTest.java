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
}
