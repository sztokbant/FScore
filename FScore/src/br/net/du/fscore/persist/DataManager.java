package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.dao.MatchDAO;
import br.net.du.fscore.persist.dao.MatchPlayerDAO;
import br.net.du.fscore.persist.dao.PlayerDAO;
import br.net.du.fscore.persist.dao.PlayerRoundDAO;
import br.net.du.fscore.persist.dao.RoundDAO;

public class DataManager {

	public static final int DATABASE_VERSION = 10;

	private Context context;
	private SQLiteDatabase db;
	private boolean useDebugDb = false;

	private PlayerDAO playerDao;
	private MatchDAO matchDao;
	private MatchPlayerDAO matchPlayerDao;
	private RoundDAO roundDao;
	private PlayerRoundDAO playerRoundDao;

	public DataManager(Context context) {
		this.context = context;
		openDb();
	}

	public DataManager(Context context, boolean useDebugDb) {
		this.context = context;
		this.useDebugDb = useDebugDb;
		openDb();
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public boolean closeDb() {
		if (db != null && db.isOpen()) {
			db.close();
			return true;
		}

		return false;
	}

	public boolean openDb() {
		if (db == null || !db.isOpen()) {
			db = new OpenHelper(context, useDebugDb).getWritableDatabase();

			// since we pass db into DAO, have to recreate DAO if db is
			// re-opened
			playerDao = new PlayerDAO(db);
			matchDao = new MatchDAO(db);
			matchPlayerDao = new MatchPlayerDAO(db);
			roundDao = new RoundDAO(db);
			playerRoundDao = new PlayerRoundDAO(db);

			return true;
		}

		return false;
	}

	// Match operations

	public long saveMatch(Match match) {
		long matchId = 0L;
		try {
			db.beginTransaction();
			matchId = matchDao.save(match);

			saveMatchPlayers(match);
			erasePlayersNotInMatchAnymore(match);
			saveMatchRounds(match);
			eraseRoundsNotInMatchAnymore(match);

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(context.getResources().getString(R.string.app_name),
					"Error saving match (transaction rolled back)", e);
			matchId = 0L;
		} finally {
			db.endTransaction();
		}

		return matchId;
	}

	public Match retrieveMatch(long matchId) {
		Match match = matchDao.retrieve(matchId);
		if (match != null) {
			match.getPlayers().addAll(retrievePlayers(match.getId()));
			List<Long> roundIds = roundDao.retrieveRoundIdsForMatch(matchId);
			for (Long roundId : roundIds) {
				match.addRound(retrieveRound(roundId));
			}
		}
		return match;
	}

	public List<Match> retrieveAllMatches() {
		List<Match> matches = new ArrayList<Match>();

		for (Match match : matchDao.retrieveAll()) {
			matches.add(retrieveMatch(match.getId()));
		}

		return matches;
	}

	public boolean deleteMatch(Match match) {
		boolean result = false;
		try {
			db.beginTransaction();
			if (match != null) {
				long matchId = match.getId();

				for (Player player : match.getPlayers()) {
					matchPlayerDao.delete(new MatchPlayerKey(matchId, player
							.getId()));
					if (matchPlayerDao.isOrphan(player)) {
						playerDao.delete(player);
					}
				}

				for (Round round : match.getRounds()) {
					deleteRound(round);
				}

				matchDao.delete(match);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(context.getResources().getString(R.string.app_name),
					"Error deleting match (transaction rolled back)", e);
		} finally {
			db.endTransaction();
		}

		return result;
	}

	// Private Match operations

	private void saveMatchPlayers(Match match) {
		for (Player player : match.getPlayers()) {
			Player dbPlayer = playerDao.find(player.getName());

			if (dbPlayer == null) {
				playerDao.save(player);
			} else {
				player = dbPlayer;
			}

			matchPlayerDao.save(new MatchPlayerKey(match.getId(), player
					.getId()));
		}
	}

	private void erasePlayersNotInMatchAnymore(Match match) {
		List<Player> dbRemainingPlayers = retrievePlayers(match.getId());
		dbRemainingPlayers.removeAll(match.getPlayers());
		for (Player player : dbRemainingPlayers) {
			matchPlayerDao.delete(new MatchPlayerKey(match.getId(), player
					.getId()));
			if (matchPlayerDao.isOrphan(player)) {
				playerDao.delete(player);
			}
		}
	}

	private void saveMatchRounds(Match match) {
		for (Round round : match.getRounds()) {
			if (!round.isPersistent()) {
				round.setMatchId(match.getId());
			}
			saveRoundNoTransaction(round);
		}
	}

	private void eraseRoundsNotInMatchAnymore(Match match) {
		List<Long> dbRoundIds = roundDao
				.retrieveRoundIdsForMatch(match.getId());
		List<Long> toDeleteRoundIds = new ArrayList<Long>();

		for (Long dbRoundId : dbRoundIds) {
			boolean deleteIt = true;
			for (Round objRound : match.getRounds()) {
				if (dbRoundId == objRound.getId()) {
					deleteIt = false;
					break;
				}
			}

			if (deleteIt) {
				toDeleteRoundIds.add(dbRoundId);
			}
		}

		if (toDeleteRoundIds.size() > 0) {
			for (Long roundId : toDeleteRoundIds) {
				// DEBUG
				Log.i("FScore", "deleting round " + roundId);
				deleteRoundById(roundId);
			}
		}
	}

	// Player operations

	List<Player> retrievePlayers(long matchId) {
		List<Long> playerIds = matchPlayerDao.retrievePlayerIds(matchId);

		List<Player> players = new ArrayList<Player>();
		PlayerDAO playerDAO = new PlayerDAO(db);

		for (Long playerId : playerIds) {
			Player player = playerDAO.retrieve(playerId);
			players.add(player);
		}

		return players;
	}

	// Round operations

	public void saveRound(Round round) {
		if (round.getMatchId() == 0) {
			Log.e(context.getResources().getString(R.string.app_name),
					"Cannot save a round that has never been saved by a match");
			return;
		}

		try {
			db.beginTransaction();
			saveRoundNoTransaction(round);
		} catch (SQLException e) {
			Log.e(context.getResources().getString(R.string.app_name),
					"Error saving round (transaction rolled back)", e);
		} finally {
			db.endTransaction();
		}
	}

	private void saveRoundNoTransaction(Round round) {
		roundDao.save(round);

		// DEBUG
		Log.i("FScore", "saving round " + round.getId());

		for (PlayerRound playerRound : round.getPlayerRounds()) {
			playerRound.setRoundId(round.getId());
			playerRoundDao.save(playerRound);

			// DEBUG
			Log.i("FScore", "saved playerround " + playerRound.getId());
		}

		List<PlayerRound> toDeleteRounds = playerRoundDao
				.retrievePlayerRoundsForRound(round.getId());
		toDeleteRounds.removeAll(round.getPlayerRounds());
		for (PlayerRound playerRound : toDeleteRounds) {
			// DEBUG
			Log.i("FScore", "deleting playerround " + playerRound.getId());
			playerRoundDao.delete(playerRound);
		}
	}

	public Round retrieveRound(long roundId) {
		Round round = roundDao.retrieve(roundId);

		if (round != null) {
			round.getPlayerRounds().addAll(
					playerRoundDao.retrievePlayerRoundsForRound(round.getId()));
		}

		return round;
	}

	private void deleteRoundById(Long roundId) {
		Round round = retrieveRound(roundId);
		deleteRound(round);
	}

	private void deleteRound(Round round) {
		for (PlayerRound playerRound : round.getPlayerRounds()) {
			playerRoundDao.delete(playerRound);
		}

		roundDao.delete(round);
	}
}
