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

	public Match getMatch(long matchId);

	public List<Match> getAllMatches();

	public long saveMatch(Match match);

	public boolean deleteMatch(Match match);

	// Player operations

	public Player getPlayer(long playerId);

	public List<Player> getAllPlayers();

	public long savePlayer(Player player);

	public void deletePlayer(Player player);
}
