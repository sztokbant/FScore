package br.net.du.fscore.test.persist.dao;

import java.util.Calendar;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.dao.MatchDAO;
import br.net.du.fscore.persist.table.MatchTable;
import br.net.du.fscore.persist.table.MatchTable.MatchColumns;

public class MatchDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	MatchDAO dao;
	Match match;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();

		MatchTable.clear(db);
		dao = new MatchDAO(db);
		match = new Match("Match Name");
		dao.save(match);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testSaveNew() {
		Cursor cursor = db.query(MatchTable.NAME, new String[] {
				BaseColumns._ID, MatchColumns.NAME, MatchColumns.DATE },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(match.getId()) }, null, null,
				null, null);

		// asserts match was saved properly
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

		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(date.getTimeInMillis() + 1000);
		match.setDate(date);

		dao.save(match);

		Cursor cursor = db.query(MatchTable.NAME, new String[] {
				BaseColumns._ID, MatchColumns.NAME, MatchColumns.DATE },
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(match.getId()) }, null, null,
				null, null);

		// asserts match was updated properly
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

		// asserts match was deleted properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());
		assertEquals(0, match.getId());

		cursor.close();
	}

	public void testRetrieve() {
		Match match2 = dao.retrieve(match.getId());
		assertEquals(match, match2);
	}

	public void testRetrieveAll() {
		List<Match> matchList = dao.retrieveAll();
		assertEquals(1, matchList.size());
		assertEquals(match, matchList.get(0));
	}
}
