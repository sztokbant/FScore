package br.net.du.fscore.persist.dao;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.dao.PlayerDAO;
import br.net.du.fscore.persist.table.PlayerTable;
import br.net.du.fscore.persist.table.PlayerTable.PlayerColumns;

public class PlayerDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	PlayerDAO dao;
	Player player;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();

		PlayerTable.clear(db);
		dao = new PlayerDAO(db);
		player = new Player("Player Name");
		dao.save(player);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testSaveNew() {
		Cursor cursor = db.query(PlayerTable.NAME, PlayerColumns.get(),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(player.getId()) }, null, null,
				null, null);

		// asserts player was saved properly
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(1, cursor.getLong(0));
		assertEquals("Player Name", cursor.getString(1));
		assertEquals(1, player.getId());

		cursor.close();
	}

	public void testSaveExisting() {
		player.setName("Name Player");
		dao.save(player);

		Cursor cursor = db.query(PlayerTable.NAME, PlayerColumns.get(),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(player.getId()) }, null, null,
				null, null);

		// asserts player was updated properly
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(1, cursor.getLong(0));
		assertEquals("Name Player", cursor.getString(1));
		assertEquals(1, player.getId());

		cursor.close();
	}

	public void testDelete() {
		dao.delete(player);

		Cursor cursor = db.query(PlayerTable.NAME, PlayerColumns.get(),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(player.getId()) }, null, null,
				null, null);

		// asserts player was deleted properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());
		assertEquals(0, player.getId());

		cursor.close();
	}

	public void testRetrieve() {
		Player player2 = dao.retrieve(player.getId());
		assertEquals(player, player2);
	}

	public void testRetrieveAll() {
		List<Player> playerList = dao.retrieveAll();
		assertEquals(1, playerList.size());
		assertEquals(player, playerList.get(0));
	}

	public void testFind() {
		Player player2 = dao.find("Player Name");
		assertEquals(player, player2);
	}
}
