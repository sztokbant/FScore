package br.net.du.fscore.test.persist;

import junit.framework.TestCase;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.net.du.fscore.persist.DataManagerImpl;

public class PlayerTableTest extends TestCase {

	protected void setUp() throws Exception {
          /* WRONG! check this: http://stackoverflow.com/questions/2095695/android-unit-tests-requiring-context
		super.setUp();
		Context context = new Activity();
		SQLiteOpenHelper openHelper = new DataManagerImpl(context).new OpenHelper(
				context, true);
		SQLiteDatabase db = openHelper.getWritableDatabase();
          */
	}

	public void testOnCreate() {
		fail("Not yet implemented");
	}

	public void testOnUpgrade() {
		fail("Not yet implemented");
	}

}
