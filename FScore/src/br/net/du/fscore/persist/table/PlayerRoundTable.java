package br.net.du.fscore.persist.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class PlayerRoundTable {
	public static final String NAME = "player_round";

	public static class PlayerRoundColumns {
		public static final String ROUND_ID = "round_id";
		public static final String PLAYER_ID = "player_id";
		public static final String BET = "bet";
		public static final String WINS = "wins";

		public static String[] get() {
			return new String[] { PlayerRoundColumns.ROUND_ID,
					PlayerRoundColumns.PLAYER_ID, PlayerRoundColumns.BET,
					PlayerRoundColumns.WINS };
		}
	}

	public static void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + PlayerRoundTable.NAME + " (");
		sb.append(PlayerRoundColumns.BET + " INTEGER NOT NULL, ");
		sb.append(PlayerRoundColumns.WINS + " INTEGER NOT NULL, ");
		sb.append(PlayerRoundColumns.ROUND_ID + " INTEGER NOT NULL, ");
		sb.append(PlayerRoundColumns.PLAYER_ID + " INTEGER NOT NULL, ");
		sb.append("FOREIGN KEY(" + PlayerRoundColumns.ROUND_ID
				+ ") REFERENCES " + RoundTable.NAME + "(" + BaseColumns._ID
				+ "), ");
		sb.append("FOREIGN KEY(" + PlayerRoundColumns.PLAYER_ID
				+ ") REFERENCES " + PlayerTable.NAME + "(" + BaseColumns._ID
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
