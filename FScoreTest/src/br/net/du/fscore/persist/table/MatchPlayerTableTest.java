package br.net.du.fscore.persist.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.table.MatchPlayerTable;
import br.net.du.fscore.persist.table.MatchPlayerTable.MatchPlayerColumns;

public class MatchPlayerTableTest extends AndroidTestCase {

	SQLiteDatabase db;
	DataManager dataManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();
		MatchPlayerTable.clear(db);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testOnCreate() {
		Cursor cursor = db.query(MatchPlayerTable.NAME, null, null, null, null,
				null, null);

		// asserts table was created properly and is empty
		assertEquals(MatchPlayerColumns.get().length, cursor.getColumnCount());
		assertEquals(0, cursor.getCount());

		cursor.close();
	}
}
