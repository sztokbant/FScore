package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.MatchPlayerTable.MatchPlayerColumns;

public class MatchPlayerDAO {
	private static final String INSERT = "INSERT INTO "
			+ MatchPlayerTable.TABLE_NAME + "(" + MatchPlayerColumns.MATCH_ID
			+ ", " + MatchPlayerColumns.PLAYER_ID + ") VALUES (?, ?)";

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public MatchPlayerDAO(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(INSERT);
	}

	public List<Player> getPlayers(long matchId) {
		List<Player> myList = new ArrayList<Player>();

		Cursor cursor = db.query(MatchPlayerTable.TABLE_NAME, new String[] {
				MatchPlayerColumns.MATCH_ID, MatchPlayerColumns.PLAYER_ID },
				MatchPlayerColumns.MATCH_ID + " = ?", // where
				new String[] { String.valueOf(matchId) }, // values
				null, // group by
				null, // having
				MatchPlayerColumns.PLAYER_ID, // order by
				null);

		if (cursor.moveToFirst()) {
			PlayerDAO playerDAO = new PlayerDAO(db);
			do {
				Player player = playerDAO.get(cursor.getLong(1));
				myList.add(player);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return myList;
	}

	public boolean exists(MatchPlayerKey key) {
		Cursor cursor = db.query(MatchPlayerTable.TABLE_NAME,
				new String[] { MatchPlayerColumns.MATCH_ID,
						MatchPlayerColumns.PLAYER_ID },
				MatchPlayerColumns.MATCH_ID + " = ?, "
						+ MatchPlayerColumns.PLAYER_ID + " = ?", // where
				new String[] { String.valueOf(key.getMatchId()),
						String.valueOf(key.getPlayerId()) }, // values
				null, // group by
				null, // having
				null, // order by
				null);

		return cursor.moveToFirst();
	}

	public long save(MatchPlayerKey key) {
		insertStatement.clearBindings();
		insertStatement.bindLong(1, key.getMatchId());
		insertStatement.bindLong(2, key.getPlayerId());
		return insertStatement.executeInsert();
	}

	public void delete(MatchPlayerKey key) {
		if (key != null) {
			db.delete(
					MatchPlayerTable.TABLE_NAME,
					MatchPlayerColumns.MATCH_ID + " = ?, "
							+ MatchPlayerColumns.PLAYER_ID + " = ?",
					new String[] { String.valueOf(key.getMatchId()),
							String.valueOf(key.getPlayerId()) });
		}
	}
}
