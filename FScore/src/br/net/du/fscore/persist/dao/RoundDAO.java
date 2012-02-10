package br.net.du.fscore.persist.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.TableColumnsUtils;
import br.net.du.fscore.persist.table.RoundTable;
import br.net.du.fscore.persist.table.RoundTable.RoundColumns;

public class RoundDAO implements Dao<Round> {

	private static final String INSERT = "INSERT INTO "
			+ RoundTable.NAME
			+ "("
			+ new TableColumnsUtils()
					.getAsCommaSeparatedStringWithoutFirstColumn(RoundColumns
							.get())
			+ ") VALUES "
			+ new TableColumnsUtils()
					.getQuestionMarksWithoutFirstColumn(RoundColumns.get());;

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public RoundDAO(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(INSERT);
	}

	@Override
	public long save(Round round) {
		if (round.getId() == 0) {
			insertStatement.clearBindings();
			insertStatement.bindLong(1, round.getNumberOfCards());
			insertStatement.bindLong(2, round.getMatchId());
			round.setId(insertStatement.executeInsert());
		} else {
			this.update(round);
		}

		return round.getId();
	}

	@Override
	public void update(Round round) {
		db.update(RoundTable.NAME, toContentValues(round), BaseColumns._ID
				+ " = ?", new String[] { String.valueOf(round.getId()) });
	}

	@Override
	public void delete(Round round) {
		if (round.isPersistent()) {
			db.delete(RoundTable.NAME, BaseColumns._ID + " = ?",
					new String[] { String.valueOf(round.getId()) });
			round.setId(0);
		}
	}

	public Round get(long id) {
		Round round = null;
		Cursor cursor = db.query(RoundTable.NAME, RoundColumns.get(),
				BaseColumns._ID + " = ?", new String[] { String.valueOf(id) },
				null, null, null, "1");
		if (cursor.moveToFirst()) {
			round = this.buildRoundFromCursor(cursor);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return round;
	}

	public List<Round> getRoundsForMatch(long matchId) {
		List<Round> myList = new ArrayList<Round>();

		Cursor cursor = db.query(RoundTable.NAME, RoundColumns.get(),
				RoundColumns.MATCH_ID + " = ?", // where
				new String[] { String.valueOf(matchId) }, // values
				null, // group by
				null, // having
				BaseColumns._ID, // order by
				null);

		if (cursor.moveToFirst()) {
			do {
				Round round = this.buildRoundFromCursor(cursor);
				myList.add(round);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return myList;
	}

	public List<Long> getRoundIdsForMatch(long matchId) {
		List<Long> myList = new ArrayList<Long>();

		Cursor cursor = db.query(RoundTable.NAME,
				new String[] { BaseColumns._ID }, RoundColumns.MATCH_ID
						+ " = ?", // where
				new String[] { String.valueOf(matchId) }, // values
				null, // group by
				null, // having
				BaseColumns._ID, // order by
				null);

		if (cursor.moveToFirst()) {
			do {
				myList.add(cursor.getLong(0));
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return myList;
	}

	private Round buildRoundFromCursor(Cursor cursor) {
		Round round = null;

		if (cursor != null) {
			round = new Round(cursor.getLong(1));
			round.setId(cursor.getLong(0));
			round.setMatchId(cursor.getLong(2));
		}

		return round;
	}

	private ContentValues toContentValues(Round round) {
		ContentValues values = new ContentValues();

		// values.put("id", a.getId()); // WRONG!
		values.put(RoundColumns.NUM_OF_CARDS, round.getNumberOfCards());
		values.put(RoundColumns.MATCH_ID, round.getMatchId());

		return values;
	}
}
