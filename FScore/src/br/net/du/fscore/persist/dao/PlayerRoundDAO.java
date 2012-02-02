package br.net.du.fscore.persist.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.persist.PlayerRoundTable;
import br.net.du.fscore.persist.PlayerTable;
import br.net.du.fscore.persist.TableColumnsUtils;
import br.net.du.fscore.persist.PlayerTable.PlayerColumns;

public class PlayerRoundDAO {
	private static final String INSERT = "INSERT INTO "
			+ PlayerRoundTable.NAME
			+ "("
			+ new TableColumnsUtils()
					.getAsCommaSeparatedStringWithoutFirstColumn(PlayerColumns
							.get()) + ") VALUES (?, ?)";

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
		// TODO
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
