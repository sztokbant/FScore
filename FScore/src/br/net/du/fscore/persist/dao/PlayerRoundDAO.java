package br.net.du.fscore.persist.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.exceptions.FScoreException;
import br.net.du.fscore.persist.TableColumnsUtils;
import br.net.du.fscore.persist.table.PlayerRoundTable;
import br.net.du.fscore.persist.table.PlayerRoundTable.PlayerRoundColumns;

public class PlayerRoundDAO implements Dao<PlayerRound> {
	private static final String INSERT = "INSERT INTO "
			+ PlayerRoundTable.NAME
			+ "("
			+ new TableColumnsUtils()
					.getAsCommaSeparatedStringWithoutFirstColumn(PlayerRoundColumns
							.get())
			+ ") VALUES "
			+ new TableColumnsUtils()
					.getQuestionMarksWithoutFirstColumn(PlayerRoundColumns
							.get());;

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public PlayerRoundDAO(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(INSERT);
	}

	@Override
	public long save(PlayerRound playerRound) {
		if (!exists(playerRound)) {
			insertStatement.clearBindings();
			insertStatement.bindLong(1, playerRound.getRoundId());
			insertStatement.bindLong(2, playerRound.getPlayer().getId());
			insertStatement.bindLong(3, playerRound.getBet());
			insertStatement.bindLong(4, playerRound.getWins());
			playerRound.setId(insertStatement.executeInsert());
		} else {
			update(playerRound);
		}

		return playerRound.getId();
	}

	@Override
	public void update(PlayerRound playerRound) {
		db.update(PlayerRoundTable.NAME, toContentValues(playerRound),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(playerRound.getId()) });
	}

	public void delete(PlayerRound playerRound) {
		if (playerRound != null) {
			db.delete(PlayerRoundTable.NAME, BaseColumns._ID + " = ?",
					new String[] { String.valueOf(playerRound.getId()) });
			playerRound.setId(0);
		}
	}

	public boolean exists(PlayerRound playerRound) {
		Cursor cursor = db.query(PlayerRoundTable.NAME,
				PlayerRoundColumns.get(), BaseColumns._ID + " = ?", // where
				new String[] { String.valueOf(playerRound.getId()) }, // values
				null, // group by
				null, // having
				null, // order by
				null);

		boolean exists = cursor.moveToFirst();

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return exists;
	}

	public PlayerRound retrieve(long id) throws FScoreException {
		PlayerRound playerRound = null;

		Cursor cursor = db.query(PlayerRoundTable.NAME,
				PlayerRoundColumns.get(), BaseColumns._ID + " = ?", // where
				new String[] { String.valueOf(id) }, // values
				null, // group by
				null, // having
				null, // order by
				null);

		if (cursor.moveToFirst()) {
			playerRound = this.buildPlayerRoundFromCursor(cursor);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return playerRound;
	}

	public List<PlayerRound> retrievePlayerRoundsForRound(long roundId) throws FScoreException {
		List<PlayerRound> myList = new ArrayList<PlayerRound>();

		Cursor cursor = db.query(PlayerRoundTable.NAME,
				PlayerRoundColumns.get(), PlayerRoundColumns.ROUND_ID + " = ?", // where
				new String[] { String.valueOf(roundId) }, // values
				null, // group by
				null, // having
				BaseColumns._ID, // order by
				null);

		if (cursor.moveToFirst()) {
			do {
				PlayerRound playerRound = this
						.buildPlayerRoundFromCursor(cursor);
				myList.add(playerRound);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return myList;
	}

	private PlayerRound buildPlayerRoundFromCursor(Cursor cursor)
			throws FScoreException {
		PlayerRound playerRound = null;

		if (cursor != null) {
			PlayerDAO playerDao = new PlayerDAO(db);
			playerRound = new PlayerRound(playerDao.retrieve(cursor.getLong(2)));

			playerRound.setId(cursor.getLong(0));
			playerRound.setRoundId(cursor.getLong(1));
			playerRound.setBet(cursor.getLong(3));
			playerRound.setWins(cursor.getLong(4));
		}

		return playerRound;
	}

	private ContentValues toContentValues(PlayerRound playerRound) {
		ContentValues values = new ContentValues();

		// values.put("id", a.getId()); // WRONG!
		values.put(PlayerRoundColumns.ROUND_ID, playerRound.getRoundId());
		values.put(PlayerRoundColumns.PLAYER_ID, playerRound.getPlayer()
				.getId());
		values.put(PlayerRoundColumns.BET, playerRound.getBet());
		values.put(PlayerRoundColumns.WINS, playerRound.getWins());

		return values;
	}
}
