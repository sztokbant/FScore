package br.net.du.fscore.test.persist;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.MatchPlayerTable;
import br.net.du.fscore.persist.MatchTable;
import br.net.du.fscore.persist.PlayerTable;

public class DataManagerImplTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManagerImpl(getContext(), true);
		db = dataManager.getDb();
		clearAllTables();
	}

	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void clearAllTables() {
		MatchPlayerTable.clear(db);
		MatchTable.clear(db);
		PlayerTable.clear(db);
	}

	public void testCloseDb() {
		assertTrue(dataManager.closeDb());
		assertFalse(dataManager.closeDb());
	}

	public void testOpenDb() {
		assertFalse(dataManager.openDb());
		dataManager.closeDb();
		assertTrue(dataManager.openDb());
	}

	public void testSaveMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		long matchId = dataManager.saveMatch(match);
		assertTrue(matchId > 0);
		assertEquals(matchId, match.getId());

		// TODO verify persistence
	}

	public void testGetMatch() {
		// TODO
	}

	public void testGetAllMatches() {
		// TODO
	}

	public void testDeleteMatch() {
		// TODO
	}

	public void testGetPlayer() {
		// TODO
	}

	public void testGetAllPlayers() {
		// TODO
	}

	public void testSavePlayer() {
		// TODO
	}

	public void testDeletePlayer() {
		// TODO
	}
}
