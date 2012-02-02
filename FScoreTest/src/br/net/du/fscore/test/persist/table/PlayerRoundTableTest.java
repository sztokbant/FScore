package br.net.du.fscore.test.persist.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.table.PlayerRoundTable;
import br.net.du.fscore.persist.table.PlayerRoundTable.PlayerRoundColumns;

public class PlayerRoundTableTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManagerImpl(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();
		PlayerRoundTable.clear(db);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testOnCreate() {
		Cursor cursor = db.query(PlayerRoundTable.NAME, null, null, null, null,
				null, null);

		// asserts table was created properly and is empty
		assertEquals(PlayerRoundColumns.get().length, cursor.getColumnCount());
		assertEquals(0, cursor.getCount());

		cursor.close();
	}
}
