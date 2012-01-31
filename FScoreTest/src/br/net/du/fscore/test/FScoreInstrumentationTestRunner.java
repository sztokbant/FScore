package br.net.du.fscore.test;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import br.net.du.fscore.test.model.MatchTest;
import br.net.du.fscore.test.model.PlayerRoundTest;
import br.net.du.fscore.test.model.PlayerTest;
import br.net.du.fscore.test.model.RoundTest;
import br.net.du.fscore.test.persist.DataManagerImplTest;
import br.net.du.fscore.test.persist.MatchDAOTest;
import br.net.du.fscore.test.persist.MatchPlayerDAOTest;
import br.net.du.fscore.test.persist.MatchPlayerTableTest;
import br.net.du.fscore.test.persist.MatchTableTest;
import br.net.du.fscore.test.persist.PlayerDAOTest;
import br.net.du.fscore.test.persist.PlayerTableTest;

public class FScoreInstrumentationTestRunner extends InstrumentationTestRunner {
	@Override
	public TestSuite getAllTests() {
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);

		// Model
		suite.addTestSuite(PlayerTest.class);
		suite.addTestSuite(PlayerRoundTest.class);
		suite.addTestSuite(RoundTest.class);
		suite.addTestSuite(MatchTest.class);

		// Tables
		suite.addTestSuite(PlayerTableTest.class);
		suite.addTestSuite(MatchTableTest.class);
		suite.addTestSuite(MatchPlayerTableTest.class);

		// DAOs
		suite.addTestSuite(PlayerDAOTest.class);
		suite.addTestSuite(MatchDAOTest.class);
		suite.addTestSuite(MatchPlayerDAOTest.class);

		suite.addTestSuite(DataManagerImplTest.class);

		return suite;
	}
}