package br.net.du.fscore.test.model;

import java.util.Calendar;

import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;

public class MatchTest extends AndroidTestCase {

	public void testEqualNamesAndDatesImplyEquivalence() {
		String name = "Match Name";
		Calendar date = Calendar.getInstance();

		Match match1 = new Match();
		match1.setName(name);
		match1.setDate(date);

		Match match2 = new Match();
		match2.setName(name);
		match2.setDate(date);

		assertEquals(match1, match2);
	}

	public void testDifferentNamesImplyDifference() {
		Match match1 = new Match();
		match1.setName("Match 1");
		Match match2 = new Match();
		match2.setName("Match 2");
		assertFalse(match1.equals(match2));
	}

}
