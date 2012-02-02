package br.net.du.fscore.persist.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class MatchTable {

	public static final String NAME = "match";

	public static class MatchColumns implements BaseColumns {
		public static final String NAME = "name";
		public static final String DATE = "date";

		public static String[] get() {
			return new String[] { BaseColumns._ID, MatchColumns.NAME,
					MatchColumns.DATE };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + MatchTable.NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(MatchColumns.NAME + " TEXT UNIQUE NOT NULL, ");
		sb.append(MatchColumns.DATE + " INTEGER NOT NULL");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MatchTable.NAME);
		onCreate(db);
	}

	public static void clear(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + MatchTable.NAME);
	}
}
