package br.net.du.fscore.test.persist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.MatchPlayerDAO;
import br.net.du.fscore.persist.MatchPlayerKey;
import br.net.du.fscore.persist.MatchPlayerTable;

public class MatchPlayerDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	MatchPlayerDAO dao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DataManagerImpl dataManager = new DataManagerImpl(getContext());
		SQLiteOpenHelper openHelper = dataManager.new OpenHelper(getContext(),
				true);
		db = openHelper.getWritableDatabase();

		dropTable();
		MatchPlayerTable.onCreate(db);

		dao = new MatchPlayerDAO(db);
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
		db.execSQL("DROP TABLE IF EXISTS " + MatchPlayerTable.NAME);
	}

	public void testSave() {
		MatchPlayerKey key = new MatchPlayerKey(7, 11);
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
		// TODO
	}
}
