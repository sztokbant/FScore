package br.net.du.fscore.persist.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.persist.TableColumnsUtils;
import br.net.du.fscore.persist.table.PlayerRoundTable;
import br.net.du.fscore.persist.table.PlayerRoundTable.PlayerRoundColumns;

public class PlayerRoundDAO {
	private static final String INSERT = "INSERT INTO "
			+ PlayerRoundTable.NAME
			+ "("
			+ new TableColumnsUtils()
					.getAsCommaSeparatedString(PlayerRoundColumns.get())
			+ ") VALUES "
			+ new TableColumnsUtils()
					.getQuestionMarks(PlayerRoundColumns.get());;

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public PlayerRoundDAO(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(INSERT);
	}

	public void save(PlayerRound playerRound) {
		// TODO
	}

	public void delete(PlayerRound playerRound) {
		if (playerRound != null) {
			db.delete(PlayerRoundTable.NAME, BaseColumns._ID + " = ?",
					new String[] { String.valueOf(playerRound.getId()) });
		}
	}

	public boolean exists(PlayerRound playerRound) {
		// TODO
		return false;
	}

	public List<Player> getPlayerRoundsForMatch(long matchId) {
		// TODO
		return null;
	}
}
