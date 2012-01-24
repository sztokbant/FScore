package br.net.du.fscore.test.model;

import br.net.du.fscore.model.Round;
import android.test.AndroidTestCase;

public class RoundTest extends AndroidTestCase {

	public void testSameNumberOfCardsAndDataImplyEquivalence() {
		int numberOfCards = 5;
		Round round1 = new Round();
		round1.setNumberOfCards(numberOfCards);
		Round round2 = new Round();
		round2.setNumberOfCards(numberOfCards);
		assertEquals(round1, round2);
	}

	public void testDifferentNumberOfCardsImplyDifference() {
		Round round1 = new Round();
		round1.setNumberOfCards(3);
		Round round2 = new Round();
		round2.setNumberOfCards(7);
		assertFalse(round1.equals(round2));
	}
}
