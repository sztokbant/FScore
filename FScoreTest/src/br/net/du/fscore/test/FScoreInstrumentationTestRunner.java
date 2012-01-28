package br.net.du.fscore.test;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import br.net.du.fscore.test.model.MatchTest;
import br.net.du.fscore.test.model.PlayerRoundTest;
import br.net.du.fscore.test.model.PlayerTest;
import br.net.du.fscore.test.model.RoundTest;
import br.net.du.fscore.test.persist.MatchTableTest;
import br.net.du.fscore.test.persist.PlayerTableTest;

public class FScoreInstrumentationTestRunner extends InstrumentationTestRunner {
	@Override
	public TestSuite getAllTests() {
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);

		suite.addTestSuite(PlayerTest.class);
		suite.addTestSuite(PlayerRoundTest.class);
		suite.addTestSuite(RoundTest.class);
		suite.addTestSuite(MatchTest.class);

		suite.addTestSuite(PlayerTableTest.class);
		suite.addTestSuite(MatchTableTest.class);

		return suite;
	}
}