package br.net.du.fscore.persist;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class PlayerRoundTable {
	public static final String NAME = "player_round";

	public static class PlayerRoundColumns implements BaseColumns {
		public static final String BET = "bet";
		public static final String WINS = "wins";
		public static final String ROUND_ID = "round_id";

		public static String[] get() {
			return new String[] { BaseColumns._ID, PlayerRoundColumns.BET,
					PlayerRoundColumns.WINS, PlayerRoundColumns.ROUND_ID };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + PlayerRoundTable.NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(PlayerRoundColumns.BET + " INTEGER NOT NULL, ");
		sb.append(PlayerRoundColumns.WINS + " INTEGER NOT NULL, ");
		sb.append(PlayerRoundColumns.ROUND_ID + " INTEGER NOT NULL, ");
		sb.append("FOREIGN KEY(" + PlayerRoundColumns.ROUND_ID
				+ ") REFERENCES " + RoundTable.NAME + "(" + BaseColumns._ID
				+ ")");
		sb.append(");");

		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PlayerRoundTable.NAME);
		onCreate(db);
	}

	public static void clear(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + PlayerRoundTable.NAME);
	}
}
