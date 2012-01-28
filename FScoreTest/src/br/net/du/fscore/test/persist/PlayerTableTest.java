package br.net.du.fscore.test.persist;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.PlayerTable;

public class PlayerTableTest extends AndroidTestCase {

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
		db.close();
	}

	private void dropTable() {
		db.execSQL("DROP TABLE IF EXISTS " + PlayerTable.NAME);
	}

	public void testOnCreate() {
		PlayerTable.onCreate(db);
		// TODO test something here...
	}
}
