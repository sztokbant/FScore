package br.net.du.fscore.persist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.net.du.fscore.model.Player;

public class PlayerDAO extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE = "player";

	public PlayerDAO(Context context) {
		super(context, "Player", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABLE + " ");
		sb.append("(id INTEGER PRIMARY KEY, ");
		sb.append("name TEXT UNIQUE NOT NULL);");

		db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO: a proper onUpgrade should be crafted...
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}

	public ContentValues toContentValues(Player player) {
		ContentValues values = new ContentValues();

		// values.put("id", a.getId()); // WRONG!
		values.put("name", player.getName());

		return values;
	}

	public void save(Player player) {
		SQLiteDatabase writableDatabase = getWritableDatabase();

		if (!player.isPersistent()) {
			long id = writableDatabase.insert(TABLE, null,
					toContentValues(player));
			player.setId(id);
		} else {
			String[] whereArgs = new String[] { Long.toString(player.getId()) };
			writableDatabase.update(TABLE, toContentValues(player), "id=?",
					whereArgs);
		}
	}

	public void delete(Player player) {
		String[] whereArgs = new String[] { Long.toString(player.getId()) };
		getWritableDatabase().delete(TABLE, "id=?", whereArgs);
	}
}
