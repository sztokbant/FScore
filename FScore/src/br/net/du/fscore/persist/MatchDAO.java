package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.persist.MatchTable.MatchColumns;

public class MatchDAO implements Dao<Match> {

	private static final String INSERT = "INSERT INTO " + MatchTable.TABLE_NAME
			+ "(" + MatchColumns.NAME + ", " + MatchColumns.DATE
			+ ") VALUES (?, ?)";

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public MatchDAO(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(MatchDAO.INSERT);
	}

	@Override
	public long save(Match match) {
		insertStatement.clearBindings();
		insertStatement.bindString(1, match.getName());
		insertStatement.bindLong(2, match.getDate().getTimeInMillis());
		return insertStatement.executeInsert();
	}

	@Override
	public void update(Match match) {
		db.update(MatchTable.TABLE_NAME, toContentValues(match),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(match.getId()) });
	}

	@Override
	public void delete(Match match) {
		if (match.getId() > 0) {
			db.delete(MatchTable.TABLE_NAME, BaseColumns._ID + " = ?",
					new String[] { String.valueOf(match.getId()) });
		}
	}

	@Override
	public Match get(long id) {
		Match match = null;
		Cursor cursor = db.query(MatchTable.TABLE_NAME, new String[] {
				BaseColumns._ID, MatchColumns.NAME, MatchColumns.DATE },
				BaseColumns._ID + " = ?", new String[] { String.valueOf(id) },
				null, null, null, "1");
		if (cursor.moveToFirst()) {
			match = this.buildMatchFromCursor(cursor);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return match;
	}

	@Override
	public List<Match> getAll() {
		List<Match> myList = new ArrayList<Match>();

		Cursor cursor = db.query(MatchTable.TABLE_NAME, new String[] {
				MatchColumns.NAME, MatchColumns.DATE }, null, // where
				null, // values
				null, // group by
				null, // having
				MatchColumns.DATE, // order by
				null);

		if (cursor.moveToFirst()) {
			do {
				Match match = this.buildMatchFromCursor(cursor);
				myList.add(match);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return myList;
	}

	private Match buildMatchFromCursor(Cursor cursor) {
		Match match = null;

		if (cursor != null) {
			match = new Match();
			match.setName(cursor.getString(0));

			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(cursor.getLong(1));
			match.setDate(date);
		}

		return match;
	}

	private ContentValues toContentValues(Match match) {
		ContentValues values = new ContentValues();

		// values.put("id", a.getId()); // WRONG!
		values.put("name", match.getName());
		values.put("date", match.getDate().getTimeInMillis());

		return values;
	}
}