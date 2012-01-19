package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;

public class DataManagerImpl implements DataManager {

	public static final int DATABASE_VERSION = 2;

	private Context context;

	private SQLiteDatabase db;

	private PlayerDAO playerDao;
	private MatchDAO matchDao;
	private MatchPlayerDAO matchPlayerDao;

	public DataManagerImpl(Context context) {
		this.context = context;

		SQLiteOpenHelper openHelper = new OpenHelper(this.context);
		db = openHelper.getWritableDatabase();

		playerDao = new PlayerDAO(db);
		matchDao = new MatchDAO(db);
		matchPlayerDao = new MatchPlayerDAO(db);
	}

	@Override
	public Match getMatch(long matchId) {
		Match match = matchDao.get(matchId);
		if (match != null) {
			match.getPlayers().addAll(matchPlayerDao.getPlayers(match.getId()));
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
	public long saveMatch(Match match) {
		long matchId = 0L;
		try {
			db.beginTransaction();
			matchId = matchDao.save(match);

			if (match.getPlayers().size() > 0) {
				for (Player player : match.getPlayers()) {
					long playerId = 0L;
					Player dbPlayer = playerDao.find(player.getName());
					if (dbPlayer == null) {
						playerId = playerDao.save(player);
					} else {
						playerId = dbPlayer.getId();
					}

					MatchPlayerKey key = new MatchPlayerKey(matchId, playerId);
					if (!matchPlayerDao.exists(key)) {
						matchPlayerDao.save(key);
					}
				}
			}

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e("FScore", "Error saving match (transaction rolled back)", e);
			matchId = 0L;
		} finally {
			db.endTransaction();
		}

		return matchId;
	}

	@Override
	public boolean deleteMatch(long matchId) {
		boolean result = false;
		try {
			db.beginTransaction();
			Match match = getMatch(matchId);
			if (match != null) {
				for (Player player : match.getPlayers()) {
					matchPlayerDao.delete(new MatchPlayerKey(match.getId(),
							player.getId()));
				}
				matchDao.delete(match);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e("FScore", "Error deleting match (transaction rolled back)", e);
		} finally {
			db.endTransaction();
		}

		return result;
	}

	@Override
	public Player getPlayer(long playerId) {
		return playerDao.get(playerId);
	}

	@Override
	public List<Player> getAllPlayers() {
		return playerDao.getAll();
	}

	@Override
	public long savePlayer(Player player) {
		return playerDao.save(player);
	}

	@Override
	public void deletePlayer(Player player) {
		playerDao.delete(player);
	}

	@Override
	public void closeDb() {
		if (db.isOpen()) {
			db.close();
		}
	}

	public class OpenHelper extends SQLiteOpenHelper {
		private Context context;

		OpenHelper(final Context context) {
			super(context, DataConstants.DATABASE_NAME, null,
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
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			MatchPlayerTable.onUpgrade(db, oldVersion, newVersion);
			MatchTable.onUpgrade(db, oldVersion, newVersion);
			PlayerTable.onUpgrade(db, oldVersion, newVersion);
		}
	}
}
