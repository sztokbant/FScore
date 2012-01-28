package br.net.du.fscore.test.persist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.MatchTable;
import br.net.du.fscore.persist.MatchTable.MatchColumns;

public class MatchTableTest extends AndroidTestCase {

	SQLiteDatabase db;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DataManagerImpl dataManager = new DataManagerImpl(getContext());
		SQLiteOpenHelper openHelper = dataManager.new OpenHelper(getContext(),
				true);
		db = openHelper.getWritableDatabase();
		dropTable();
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

	public void testOnCreate() {
		MatchTable.onCreate(db);

		Cursor cursor = db.query(MatchTable.NAME, null, null, null, null, null,
				null);

		// asserts table was created properly and is empty
		assertEquals(MatchColumns.get().length, cursor.getColumnCount());
		assertEquals(0, cursor.getCount());

		cursor.close();
	}
}
