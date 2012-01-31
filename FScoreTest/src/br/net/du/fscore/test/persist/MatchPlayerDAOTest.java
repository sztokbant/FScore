package br.net.du.fscore.test.persist;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.MatchPlayerDAO;
import br.net.du.fscore.persist.MatchPlayerKey;
import br.net.du.fscore.persist.MatchPlayerTable;
import br.net.du.fscore.persist.PlayerDAO;
import br.net.du.fscore.persist.PlayerTable;

public class MatchPlayerDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManagerImpl dataManager;

	MatchPlayerDAO dao;
	MatchPlayerKey key;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManagerImpl(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();

		MatchPlayerTable.clear(db);

		dao = new MatchPlayerDAO(db);
		key = new MatchPlayerKey(7, 11);
	}

	@Override
	protected void tearDown() throws Exception {
		MatchPlayerTable.clear(db);
		dataManager.closeDb();
		super.tearDown();
	}

	public void testSave() {
		dao.save(key);

		Cursor cursor = db.query(MatchPlayerTable.NAME, null, null, null, null,
				null, null);

		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(7, cursor.getLong(0));
		assertEquals(11, cursor.getLong(1));

		cursor.close();
	}

	public void testDelete() {
		dao.save(key);
		dao.delete(key);

		Cursor cursor = db.query(MatchPlayerTable.NAME, null, null, null, null,
				null, null);

		// asserts player was deleted properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());

		cursor.close();
	}

	public void testExists() {
		dao.save(key);
		assertTrue(dao.exists(key));
		dao.delete(key);
		assertFalse(dao.exists(key));
	}

	public void testIsOrphan() {
		Player player = new Player("Dummy Player");
		player.setId(11);
		assertTrue(dao.isOrphan(player));
		dao.save(key);
		assertFalse(dao.isOrphan(player));
	}

	public void testGetPlayers() {
		PlayerTable.clear(db);

		List<Player> players = new ArrayList<Player>();
		players.add(new Player("Dummy 1"));
		players.add(new Player("Dummy 2"));
		players.add(new Player("Dummy 3"));

		PlayerDAO playerDao = new PlayerDAO(db);
		for (Player p : players) {
			playerDao.save(p);
			key.setPlayerId(p.getId());
			dao.save(key);
		}

		assertEquals(players, dao.getPlayers(key.getMatchId()));

		PlayerTable.clear(db);
	}
}
