package br.net.du.fscore.model;

import android.test.AndroidTestCase;

public class PlayerTest extends AndroidTestCase {

	private Player player1;
	private Player player2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String name = "Player Name";
		player1 = new Player(name);
		player2 = new Player(name);
	}

	public void testEquals() {
		assertEquals(player1, player2);
	}

	public void testHashCode() {
		assertEquals(player1.hashCode(), player2.hashCode());
	}

	public void testEqualsForSameObject() {
		assertEquals(player1, player1);
	}

	public void testHashCodeForSameObject() {
		assertEquals(player1.hashCode(), player1.hashCode());
	}

	public void testDifferentIdsDoesntImplyDifference() {
		assertEquals(player1, player2);
		player2.setId(player1.getId() + 31);
		assertTrue(player1.equals(player2));
	}

	public void testDifferentNamesImpliesDifference() {
		assertEquals(player1, player2);
		player2.setName("Other Player");
		assertFalse(player1.equals(player2));
	}

	public void testPlayerNameCannotBeBlank() {
		try {
			new Player("  ");
			fail("should have thrown an exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testPlayerNameMustBeTrimmed() {
		Player player = new Player("  Player name  ");
		assertEquals("Player name", player.getName());
	}

	public void testDifferentCasesInPlayerNamesStillImplyEquivalence() {
		Player player1 = new Player("CaSe");
		Player player2 = new Player("cAsE");
		assertTrue(player1.equals(player2));
	}
}
