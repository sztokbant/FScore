package br.net.du.fscore.test.persist;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManagerImpl;

public class DataManagerImplTest extends AndroidTestCase {
	DataManagerImpl dataManager;
	SQLiteDatabase db;

	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManagerImpl(getContext());

		SQLiteOpenHelper openHelper = dataManager.new OpenHelper(getContext(),
				true);
		db = openHelper.getWritableDatabase();

		dataManager.dropAllTables();
	}

	protected void tearDown() throws Exception {
		dataManager.dropAllTables();
		dataManager.closeDb();
		super.tearDown();
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
	}

	public void testGetMatch() {

	}

	public void testGetAllMatches() {
		fail("Not yet implemented");
	}

	public void testDeleteMatch() {
		fail("Not yet implemented");
	}

	public void testGetPlayer() {
		fail("Not yet implemented");
	}

	public void testGetAllPlayers() {
		fail("Not yet implemented");
	}

	public void testSavePlayer() {
		fail("Not yet implemented");
	}

	public void testDeletePlayer() {
		fail("Not yet implemented");
	}

}
