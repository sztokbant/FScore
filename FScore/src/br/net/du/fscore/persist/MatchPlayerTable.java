package br.net.du.fscore.persist;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class MatchPlayerTable {

	public static final String TABLE_NAME = "match_player";

	public static class MatchPlayerColumns {
		public static final String MATCH_ID = "match_id";
		public static final String PLAYER_ID = "player_id";
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + MatchPlayerTable.TABLE_NAME + " (");
		sb.append(MatchPlayerColumns.MATCH_ID + " INTEGER NOT NULL, ");
		sb.append(MatchPlayerColumns.PLAYER_ID + " INTEGER NOT NULL, ");
		sb.append("FOREIGN KEY(" + MatchPlayerColumns.MATCH_ID
				+ ") REFERENCES " + MatchTable.TABLE_NAME + "("
				+ BaseColumns._ID + "), ");
		sb.append("FOREIGN KEY(" + MatchPlayerColumns.PLAYER_ID
				+ ") REFERENCES " + PlayerTable.TABLE_NAME + "("
				+ BaseColumns._ID + "), ");
		sb.append("PRIMARY KEY (" + MatchPlayerColumns.MATCH_ID + ", "
				+ MatchPlayerColumns.PLAYER_ID + ")");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MatchPlayerTable.TABLE_NAME);
		onCreate(db);
	}
}
