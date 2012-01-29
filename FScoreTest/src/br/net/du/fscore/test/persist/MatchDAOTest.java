package br.net.du.fscore.test.persist;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.MatchDAO;
import br.net.du.fscore.persist.MatchTable;
import br.net.du.fscore.persist.MatchTable.MatchColumns;

public class MatchDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	MatchDAO dao;
	Match match;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DataManagerImpl dataManager = new DataManagerImpl(getContext());
		SQLiteOpenHelper openHelper = dataManager.new OpenHelper(getContext(),
				true);
		db = openHelper.getWritableDatabase();

		dropTable();
		MatchTable.onCreate(db);

		dao = new MatchDAO(db);
		match = new Match("Match Name");
		dao.save(match);
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
		db.execSQL("DROP TABLE IF EXISTS " + MatchTable.NAME);
	}

	public void testSaveNew() {
		Cursor cursor = db.query(MatchTable.NAME, new String[] {
				BaseColumns._ID, MatchColumns.NAME, MatchColumns.DATE },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(match.getId()) }, null, null,
				null, null);

		// asserts Player was saved properly
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(1, cursor.getLong(0));
		assertEquals("Match Name", cursor.getString(1));
		assertEquals(match.getDate().getTimeInMillis(), cursor.getLong(2));
		assertEquals(1, match.getId());

		cursor.close();
	}

	public void testSaveExisting() {
		match.setName("Name Match");
		dao.save(match);

		Cursor cursor = db.query(MatchTable.NAME, new String[] {
				BaseColumns._ID, MatchColumns.NAME, MatchColumns.DATE },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(match.getId()) }, null, null,
				null, null);

		// asserts player was updated properly
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(1, cursor.getLong(0));
		assertEquals("Name Match", cursor.getString(1));
		assertEquals(match.getDate().getTimeInMillis(), cursor.getLong(2));
		assertEquals(1, match.getId());

		cursor.close();
	}

	public void testDelete() {
		dao.delete(match);

		Cursor cursor = db.query(MatchTable.NAME, new String[] {
				BaseColumns._ID, MatchColumns.NAME, MatchColumns.DATE },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(match.getId()) }, null, null,
				null, null);

		// asserts player was updated properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());
		assertEquals(0, match.getId());

		cursor.close();
	}

	public void testGet() {
		Match match2 = dao.get(match.getId());
		assertEquals(match, match2);
	}

	public void testGetAll() {
		List<Match> matchList = dao.getAll();
		assertEquals(1, matchList.size());
		assertEquals(match, matchList.get(0));
	}
}
