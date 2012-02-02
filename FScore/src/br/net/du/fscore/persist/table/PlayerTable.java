package br.net.du.fscore.persist.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class PlayerTable {

	public static final String NAME = "player";

	public static class PlayerColumns implements BaseColumns {
		public static final String NAME = "name";

		public static String[] get() {
			return new String[] { BaseColumns._ID, PlayerColumns.NAME };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + PlayerTable.NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(PlayerColumns.NAME + " TEXT UNIQUE NOT NULL");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PlayerTable.NAME);
		onCreate(db);
	}

	public static void clear(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + PlayerTable.NAME);
	}
}
