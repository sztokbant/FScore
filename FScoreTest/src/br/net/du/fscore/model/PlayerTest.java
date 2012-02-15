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
}
