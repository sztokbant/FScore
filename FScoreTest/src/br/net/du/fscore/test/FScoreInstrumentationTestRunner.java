package br.net.du.fscore.test;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import br.net.du.fscore.test.model.MatchTest;
import br.net.du.fscore.test.model.PlayerTest;
import br.net.du.fscore.test.model.RoundTest;

public class FScoreInstrumentationTestRunner extends InstrumentationTestRunner {
	@Override
	public TestSuite getAllTests() {
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);

		suite.addTestSuite(PlayerTest.class);
		suite.addTestSuite(RoundTest.class);
		suite.addTestSuite(MatchTest.class);

		return suite;
	}
}