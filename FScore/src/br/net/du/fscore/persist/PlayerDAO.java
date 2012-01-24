package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.PlayerTable.PlayerColumns;

public class PlayerDAO implements Dao<Player> {

	private static final String INSERT = "INSERT INTO " + PlayerTable.NAME
			+ "(" + PlayerColumns.NAME + ") VALUES (?)";

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public PlayerDAO(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(INSERT);
	}

	public long save(Player player) {
		if (player.getId() == 0) {
			insertStatement.clearBindings();
			insertStatement.bindString(1, player.getName());
			player.setId(insertStatement.executeInsert());
		} else {
			this.update(player);
		}

		return player.getId();
	}

	@Override
	public void update(Player player) {
//		db.update(PlayerTable.NAME, toContentValues(player), BaseColumns._ID
//				+ " = ?", new String[] { String.valueOf(player.getId()) });
	}

	public void delete(Player player) {
		if (player.isPersistent()) {
			db.delete(MatchTable.NAME, BaseColumns._ID + " = ?",
					new String[] { String.valueOf(player.getId()) });
		}
	}

	@Override
	public Player get(long id) {
		Player player = null;
		Cursor cursor = db.query(PlayerTable.NAME, PlayerColumns.get(),
				BaseColumns._ID + " = ?", new String[] { String.valueOf(id) },
				null, null, null, "1");
		if (cursor.moveToFirst()) {
			player = this.buildPlayerFromCursor(cursor);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return player;
	}

	@Override
	public List<Player> getAll() {
		List<Player> myList = new ArrayList<Player>();

		Cursor cursor = db.query(PlayerTable.NAME, PlayerColumns.get(), null, // where
				null, // values
				null, // group by
				null, // having
				PlayerColumns.NAME, // order by
				null);

		if (cursor.moveToFirst()) {
			do {
				Player player = this.buildPlayerFromCursor(cursor);
				myList.add(player);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return myList;
	}

	public Player find(String name) {
		long playerId = 0L;

		Cursor cursor = db.query(PlayerTable.NAME, PlayerColumns.get(),
				PlayerColumns.NAME + " = ?", // where
				new String[] { name }, // values
				null, // group by
				null, // having
				null, // order by
				"1"); // limit

		if (cursor.moveToFirst()) {
			playerId = cursor.getLong(0);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return this.get(playerId);
	}

	private Player buildPlayerFromCursor(Cursor cursor) {
		Player player = null;

		if (cursor != null) {
			player = new Player();
			player.setId(cursor.getLong(0));
			player.setName(cursor.getString(1));
		}

		return player;
	}

	private ContentValues toContentValues(Player player) {
		ContentValues values = new ContentValues();

		// values.put("id", a.getId()); // WRONG!
		values.put(PlayerColumns.NAME, player.getName());

		return values;
	}
}
