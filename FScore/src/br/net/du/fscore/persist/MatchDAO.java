package br.net.du.fscore.persist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.net.du.fscore.model.Match;

public class MatchDAO extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE = "match";

	public MatchDAO(Context context) {
		super(context, "Match", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABLE + " ");
		sb.append("(id INTEGER PRIMARY KEY, ");
		sb.append("name TEXT NOT NULL, ");
		sb.append("date DATETIME NOT NULL);");

		db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}

	public ContentValues toContentValues(Match match) {
		ContentValues values = new ContentValues();

		// values.put("id", a.getId()); // WRONG!
		values.put("name", match.getName());
		values.put("date", match.getDate().toString());

		return values;
	}

	public void save(Match match) {
		SQLiteDatabase writableDatabase = getWritableDatabase();

		if (!match.isPersistent()) {
			long id = writableDatabase.insert(TABLE, null,
					toContentValues(match));
			match.setId(id);
		} else {
			String[] whereArgs = new String[] { Long.toString(match.getId()) };
			writableDatabase.update(TABLE, toContentValues(match), "id=?",
					whereArgs);
		}
	}

	public void delete(Match match) {
		String[] whereArgs = new String[] { Long.toString(match.getId()) };
		getWritableDatabase().delete(TABLE, "id=?", whereArgs);
	}
}
