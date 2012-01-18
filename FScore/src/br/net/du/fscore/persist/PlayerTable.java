package br.net.du.fscore.persist;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class PlayerTable {

	public static final String TABLE_NAME = "player";

	public static class PlayerColumns implements BaseColumns {
		public static final String NAME = "name";
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + PlayerTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(PlayerColumns.NAME + " TEXT UNIQUE NOT NULL");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PlayerTable.TABLE_NAME);
		PlayerTable.onCreate(db);
	}
}
