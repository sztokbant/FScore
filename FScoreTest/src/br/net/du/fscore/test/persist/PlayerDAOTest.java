package br.net.du.fscore.test.persist;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.PlayerDAO;
import br.net.du.fscore.persist.PlayerTable;
import br.net.du.fscore.persist.PlayerTable.PlayerColumns;

public class PlayerDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	PlayerDAO dao;
	Player player;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DataManagerImpl dataManager = new DataManagerImpl(getContext());
		SQLiteOpenHelper openHelper = dataManager.new OpenHelper(getContext(),
				true);
		db = openHelper.getWritableDatabase();

		dropTable();
		PlayerTable.onCreate(db);

		dao = new PlayerDAO(db);
		player = new Player("Player Name");
		dao.save(player);
	}

	@Override
	protected void tearDown() throws Exception {
		dropTable();
		if (db.isOpen()) {
			db.close();
		}
		super.tearDown();
	}

	private void dropTable() {
		db.execSQL("DROP TABLE IF EXISTS " + PlayerTable.NAME);
	}

	public void testSaveNew() {
		Cursor cursor = db.query(PlayerTable.NAME, new String[] {
				BaseColumns._ID, PlayerColumns.NAME },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(player.getId()) }, null, null,
				null, null);

		// asserts Player was saved properly
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

		Cursor cursor = db.query(PlayerTable.NAME, new String[] {
				BaseColumns._ID, PlayerColumns.NAME },
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

		Cursor cursor = db.query(PlayerTable.NAME, new String[] {
				BaseColumns._ID, PlayerColumns.NAME },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(player.getId()) }, null, null,
				null, null);

		// asserts player was updated properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());
		assertEquals(0, player.getId());

		cursor.close();
	}

	public void testGet() {
		Player player2 = dao.get(player.getId());
		assertEquals(player, player2);
	}

	public void testGetAll() {
		List<Player> playerList = dao.getAll();
		assertEquals(1, playerList.size());
		assertEquals(player, playerList.get(0));
	}

	public void testFind() {
		Player player2 = dao.find("Player Name");
		assertEquals(player, player2);
	}
}
