package br.net.du.fscore.persist;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;

public class DataManagerImpl implements DataManager {

	public static final int DATABASE_VERSION = 0;

	private Context context;

	private SQLiteDatabase db;

	private PlayerDAO playerDao;
	private MatchDAO matchDao;

	// TODO
	// private MatchPlayerDAO matchPlayerDao;

	public DataManagerImpl(Context context) {
		this.context = context;

		SQLiteOpenHelper openHelper = new OpenHelper(this.context);
		db = openHelper.getWritableDatabase();

		matchDao = new MatchDAO(db);
		// TODO
		// playerDAO = new PlayerDAO(db);
		// matchPlayerDAO = new MatchPlayerDAO(db);
	}

	@Override
	public Match getMatch(long matchId) {
		Match match = matchDao.get(matchId);
		// TODO
		// if (match != null) {
		// match.getPlayers().addAll(matchPlayerDao.getPlayers(match.getId()));
		// }
		return match;
	}

	@Override
	public List<Match> getAllMatches() {
		return matchDao.getAll();
	}

	@Override
	public long saveMatch(Match match) {
		long matchId = 0L;
		try {
			db.beginTransaction();
			matchId = matchDao.save(match);

			// TODO
			// if (match.getPlayers().size() > 0) {
			// }

			db.setTransactionSuccessful();
			// } catch (SQLException e) {
			// TODO
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
				// TODO
			}
			matchDao.delete(match);
			db.setTransactionSuccessful();
			result = true;
			// } catch (SQLException e) {
			// TODO
		} finally {
			db.endTransaction();
		}

		return result;
	}

	@Override
	public Player getPlayer(long playerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Player> getAllPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long savePlayer(Player player) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deletePlayer(Player player) {
		// TODO Auto-generated method stub

	}

}
