package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.dao.MatchDAO;
import br.net.du.fscore.persist.dao.MatchPlayerDAO;
import br.net.du.fscore.persist.dao.PlayerDAO;
import br.net.du.fscore.persist.dao.PlayerRoundDAO;
import br.net.du.fscore.persist.dao.RoundDAO;
import br.net.du.fscore.persist.table.MatchPlayerTable;
import br.net.du.fscore.persist.table.MatchTable;
import br.net.du.fscore.persist.table.PlayerRoundTable;
import br.net.du.fscore.persist.table.PlayerTable;
import br.net.du.fscore.persist.table.RoundTable;

public class DataManagerImpl implements DataManager {

	public static final int DATABASE_VERSION = 10;

	private Context context;
	private SQLiteDatabase db;
	private boolean useDebugDb = false;

	private PlayerDAO playerDao;
	private MatchDAO matchDao;
	private MatchPlayerDAO matchPlayerDao;
	private RoundDAO roundDao;
	private PlayerRoundDAO playerRoundDao;

	public DataManagerImpl(Context context) {
		this.context = context;
		openDb();
	}

	public DataManagerImpl(Context context, boolean useDebugDb) {
		this.context = context;
		this.useDebugDb = useDebugDb;
		openDb();
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	@Override
	public boolean closeDb() {
		if (db != null && db.isOpen()) {
			db.close();
			return true;
		}

		return false;
	}

	@Override
	public boolean openDb() {
		if (db == null || !db.isOpen()) {
			db = new OpenHelper(this.context, useDebugDb).getWritableDatabase();

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

	@Override
	public long saveMatch(Match match) {
		long matchId = 0L;
		try {
			db.beginTransaction();
			matchId = matchDao.save(match);

			storeNewPlayersForMatch(match);
			eraseRemovedPlayersFromMatch(match);
			storeNewRoundsForMatch(match);
			eraseRemovedRoundsFromMatch(match);

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

	private void storeNewPlayersForMatch(Match match) {
		if (match.getPlayers().size() > 0) {
			for (Player player : match.getPlayers()) {
				if (!player.isPersistent()) {
					Log.i(context.getResources().getString(R.string.app_name),
							"storing player [" + player.getName() + "]");
					Player dbPlayer = playerDao.find(player.getName());
					if (dbPlayer == null) {
						Log.i(context.getResources().getString(
								R.string.app_name), "added new player ["
								+ player.getName() + "]");
						playerDao.save(player);
					} else {
						Log.i(context.getResources().getString(
								R.string.app_name), "player already exists ["
								+ player.getName() + "]");
						player = dbPlayer;
					}
				}

				matchPlayerDao.save(new MatchPlayerKey(match.getId(), player
						.getId()));
			}
		}
	}

	private void eraseRemovedPlayersFromMatch(Match match) {
		List<Player> dbRemainingPlayers = matchPlayerDao.getPlayers(match
				.getId());
		dbRemainingPlayers.removeAll(match.getPlayers());
		if (dbRemainingPlayers.size() > 0) {
			for (Player player : dbRemainingPlayers) {
				matchPlayerDao.delete(new MatchPlayerKey(match.getId(), player
						.getId()));
				if (matchPlayerDao.isOrphan(player)) {
					playerDao.delete(player);
				}
				Log.i(context.getResources().getString(R.string.app_name),
						"deleted player [" + player + "]");
			}
		}
	}

	private void storeNewRoundsForMatch(Match match) {
		if (match.getRounds().size() > 0) {
			for (Round round : match.getRounds()) {
				if (!round.isPersistent()) {
					round.setMatchId(match.getId());
					saveRound(round);
					Log.i(context.getResources().getString(R.string.app_name),
							"saved round [" + round.getId() + "]");
				}
			}
		}
	}

	private void saveRound(Round round) {
		roundDao.save(round);
		// TODO: store related PlayerRounds
	}

	private void eraseRemovedRoundsFromMatch(Match match) {
		List<Round> dbRounds = roundDao.getRoundsForMatch(match.getId());
		List<Round> toDeleteRounds = new ArrayList<Round>();

		for (Round dbRound : dbRounds) {
			boolean deleteIt = true;
			for (Round objRound : match.getRounds()) {
				if (dbRound.getId() == objRound.getId()) {
					deleteIt = false;
					break;
				}
			}

			if (deleteIt) {
				toDeleteRounds.add(dbRound);
			}
		}

		if (toDeleteRounds.size() > 0) {
			for (Round round : toDeleteRounds) {
				Log.i(context.getResources().getString(R.string.app_name),
						"deleting round [" + round.getId() + "]");
				eraseRound(round);
			}
		}
	}

	private void eraseRound(Round round) {
		roundDao.delete(round);
		// TODO: delete related PlayerRounds
	}

	@Override
	public Match getMatch(long matchId) {
		Match match = matchDao.get(matchId);
		if (match != null) {
			match.getPlayers().addAll(matchPlayerDao.getPlayers(match.getId()));
			match.getRounds().addAll(roundDao.getRoundsForMatch(match.getId()));
		}
		return match;
	}

	@Override
	public List<Match> getAllMatches() {
		List<Match> matches = new ArrayList<Match>();

		for (Match match : matchDao.getAll()) {
			matches.add(this.getMatch(match.getId()));
		}

		return matches;
	}

	@Override
	public boolean deleteMatch(Match match) {
		boolean result = false;
		try {
			db.beginTransaction();
			if (match != null) {
				long matchId = match.getId();

				matchDao.delete(match);
				for (Player p : match.getPlayers()) {
					matchPlayerDao
							.delete(new MatchPlayerKey(matchId, p.getId()));
					if (matchPlayerDao.isOrphan(p)) {
						playerDao.delete(p);
					}
				}

				Log.i(context.getResources().getString(R.string.app_name),
						"deleted match [" + match + "]");
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

	private class OpenHelper extends SQLiteOpenHelper {
		private Context context;

		OpenHelper(final Context context, boolean useDebugDatabase) {
			super(context, useDebugDatabase ? DataConstants.DEBUG_DATABASE_NAME
					: DataConstants.DATABASE_NAME, null,
					DataManagerImpl.DATABASE_VERSION);
			this.context = context;
		}

		// onOpen available if needed
		@Override
		public void onOpen(final SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			PlayerTable.onCreate(db);
			MatchTable.onCreate(db);
			MatchPlayerTable.onCreate(db);
			RoundTable.onCreate(db);
			PlayerRoundTable.onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			MatchPlayerTable.onUpgrade(db, oldVersion, newVersion);
			MatchTable.onUpgrade(db, oldVersion, newVersion);
			PlayerTable.onUpgrade(db, oldVersion, newVersion);
			RoundTable.onUpgrade(db, oldVersion, newVersion);
			PlayerRoundTable.onUpgrade(db, oldVersion, newVersion);
		}
	}
}
