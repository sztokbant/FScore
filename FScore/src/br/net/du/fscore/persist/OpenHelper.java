package br.net.du.fscore.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

	private Context context;

	OpenHelper(final Context context) {
		super(context, DataConstants.DATABASE_NAME, null,
				DataManager.DATABASE_VERSION);
		this.context = context;
	}

	// onOpen available if needed
	@Override
	public void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		PlayerTable.onCreate(db);
		MatchTable.onCreate(db);
		MatchPlayerTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		MatchPlayerTable.onUpgrade(db, oldVersion, newVersion);
		MatchTable.onUpgrade(db, oldVersion, newVersion);
		PlayerTable.onUpgrade(db, oldVersion, newVersion);
	}
}
