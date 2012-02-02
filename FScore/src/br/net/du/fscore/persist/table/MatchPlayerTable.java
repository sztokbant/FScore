package br.net.du.fscore.persist.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class MatchPlayerTable {

	public static final String NAME = "match_player";

	public static class MatchPlayerColumns {
		public static final String MATCH_ID = "match_id";
		public static final String PLAYER_ID = "player_id";

		public static String[] get() {
			return new String[] { MatchPlayerColumns.MATCH_ID,
					MatchPlayerColumns.PLAYER_ID };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + MatchPlayerTable.NAME + " (");
		sb.append(MatchPlayerColumns.MATCH_ID + " INTEGER NOT NULL, ");
		sb.append(MatchPlayerColumns.PLAYER_ID + " INTEGER NOT NULL, ");
		sb.append("FOREIGN KEY(" + MatchPlayerColumns.MATCH_ID
				+ ") REFERENCES " + MatchTable.NAME + "(" + BaseColumns._ID
				+ "), ");
		sb.append("FOREIGN KEY(" + MatchPlayerColumns.PLAYER_ID
				+ ") REFERENCES " + PlayerTable.NAME + "(" + BaseColumns._ID
				+ "), ");
		sb.append("PRIMARY KEY (" + MatchPlayerColumns.MATCH_ID + ", "
				+ MatchPlayerColumns.PLAYER_ID + ")");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MatchPlayerTable.NAME);
		onCreate(db);
	}

	public static void clear(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + MatchPlayerTable.NAME);
	}
}
