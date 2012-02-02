package br.net.du.fscore.persist.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class RoundTable {

	public static final String NAME = "round";

	public static class RoundColumns implements BaseColumns {
		public static final String NUM_OF_CARDS = "num_of_cards";
		public static final String MATCH_ID = "match_id";

		public static String[] get() {
			return new String[] { BaseColumns._ID, RoundColumns.NUM_OF_CARDS,
					RoundColumns.MATCH_ID };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + RoundTable.NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(RoundColumns.NUM_OF_CARDS + " INTEGER NOT NULL, ");
		sb.append(RoundColumns.MATCH_ID + " INTEGER NOT NULL, ");
		sb.append("FOREIGN KEY(" + RoundColumns.MATCH_ID + ") REFERENCES "
				+ MatchTable.NAME + "(" + BaseColumns._ID + ")");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + RoundTable.NAME);
		onCreate(db);
	}

	public static void clear(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + RoundTable.NAME);
	}
}
