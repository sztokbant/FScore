package br.net.du.fscore.persist.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.table.PlayerTable;
import br.net.du.fscore.persist.table.PlayerTable.PlayerColumns;

public class PlayerTableTest extends AndroidTestCase {

	SQLiteDatabase db;
	DataManager dataManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();
		PlayerTable.clear(db);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testOnCreate() {
		Cursor cursor = db.query(PlayerTable.NAME, null, null, null, null,
				null, null);

		// asserts table was created properly and is empty
		assertEquals(PlayerColumns.get().length, cursor.getColumnCount());
		assertEquals(0, cursor.getCount());

		cursor.close();
	}
}
