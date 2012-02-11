package br.net.du.fscore.persist.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.MatchPlayerKey;
import br.net.du.fscore.persist.dao.MatchPlayerDAO;
import br.net.du.fscore.persist.table.MatchPlayerTable;

public class MatchPlayerDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	MatchPlayerDAO dao;
	MatchPlayerKey key;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();

		MatchPlayerTable.clear(db);
		dao = new MatchPlayerDAO(db);
		key = new MatchPlayerKey(7, 11);
	}

	@Override
	protected void tearDown() throws Exception {
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
}
