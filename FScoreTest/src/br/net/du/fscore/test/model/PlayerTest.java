package br.net.du.fscore.test.model;

import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;

public class PlayerTest extends AndroidTestCase {

	public void testEqualNamesImplyEquivalence() {
		String name = "Player Name";
		Player player1 = new Player();
		player1.setName(name);
		Player player2 = new Player();
		player2.setName(name);
		assertEquals(player1, player2);
	}

	public void testDifferentNamesImplyDifference() {
		Player player1 = new Player();
		player1.setName("Player 1");
		Player player2 = new Player();
		player2.setName("Player 2");
		assertFalse(player1.equals(player2));
	}
}
