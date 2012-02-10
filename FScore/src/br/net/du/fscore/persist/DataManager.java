package br.net.du.fscore.persist;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;

public interface DataManager {

	// DB operations

	boolean closeDb();

	boolean openDb();

	SQLiteDatabase getDb();

	// Match operations

	public long saveMatch(Match match);

	public Match getMatch(long matchId);

	public List<Match> getAllMatches();

	public boolean deleteMatch(Match match);

	public List<Player> getPlayers(long matchId);
}
