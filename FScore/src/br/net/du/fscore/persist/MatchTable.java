package br.net.du.fscore.persist;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class MatchTable {

	public static final String TABLE_NAME = "match";

	public static class MatchColumns implements BaseColumns {
		public static final String NAME = "name";
		public static final String DATE = "date";

		public static String[] get() {
			return new String[] { BaseColumns._ID, NAME, DATE };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + MatchTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(MatchColumns.NAME + " TEXT UNIQUE NOT NULL, ");
		sb.append(MatchColumns.DATE + " INTEGER NOT NULL");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MatchTable.TABLE_NAME);
		onCreate(db);
	}
}
