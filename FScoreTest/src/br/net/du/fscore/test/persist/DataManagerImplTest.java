package br.net.du.fscore.test.persist;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.DataManagerImpl;
import br.net.du.fscore.persist.MatchDAO;
import br.net.du.fscore.persist.MatchPlayerDAO;
import br.net.du.fscore.persist.MatchPlayerKey;
import br.net.du.fscore.persist.MatchPlayerTable;
import br.net.du.fscore.persist.MatchTable;
import br.net.du.fscore.persist.PlayerDAO;
import br.net.du.fscore.persist.PlayerTable;

public class DataManagerImplTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	MatchDAO matchDao;
	PlayerDAO playerDao;
	MatchPlayerDAO matchPlayerDao;

	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManagerImpl(getContext(), true);
		db = dataManager.getDb();
		clearAllTables();

		matchDao = new MatchDAO(db);
		playerDao = new PlayerDAO(db);
		matchPlayerDao = new MatchPlayerDAO(db);
	}

	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void clearAllTables() {
		MatchPlayerTable.clear(db);
		MatchTable.clear(db);
		PlayerTable.clear(db);
	}

	public void testCloseDb() {
		assertTrue(dataManager.closeDb());
		assertFalse(dataManager.closeDb());
	}

	public void testOpenDb() {
		assertFalse(dataManager.openDb());
		dataManager.closeDb();
		assertTrue(dataManager.openDb());
	}

	public void testSaveNewMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		long matchId = dataManager.saveMatch(match);

		assertTrue(matchId > 0);
		assertEquals(matchId, match.getId());
		assertTrue(player.getId() > 0);

		Match match2 = matchDao.get(matchId);
		match2.withPlayer(playerDao.get(player.getId()));
		assertEquals(match, match2);

		MatchPlayerKey key = new MatchPlayerKey(matchId, player.getId());
		assertTrue(matchPlayerDao.exists(key));
	}

	public void testGetMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		dataManager.saveMatch(match);

		assertEquals(match, dataManager.getMatch(match.getId()));
	}

	public void testGetAllMatches() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		List<Match> matches = new ArrayList<Match>();
		matches.add(match);

		dataManager.saveMatch(match);

		assertEquals(matches, dataManager.getAllMatches());
	}

	public void testDeleteMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		long matchId = dataManager.saveMatch(match);
		long playerId = player.getId();
		MatchPlayerKey key = new MatchPlayerKey(matchId, playerId);

		dataManager.deleteMatch(match);

		assertEquals(0, match.getId());
		assertEquals(0, player.getId());
		assertNull(matchDao.get(matchId));
		assertNull(playerDao.get(playerId));
		assertFalse(matchPlayerDao.exists(key));
	}

	public void testSaveNewPlayer() {
		Player player = new Player("A Player");
		long playerId = dataManager.savePlayer(player);

		assertTrue(player.getId() > 0);
		assertEquals(playerId, player.getId());
		assertEquals(player, playerDao.get(playerId));
	}

	public void testGetPlayer() {
		Player player = new Player("A Player");
		dataManager.savePlayer(player);
		assertEquals(player, dataManager.getPlayer(player.getId()));
	}

	public void testGetAllPlayers() {
		Player player = new Player("A Player");

		List<Player> players = new ArrayList<Player>();
		players.add(player);

		dataManager.savePlayer(player);

		assertEquals(players, dataManager.getAllPlayers());
	}

	public void testDeletePlayer() {
		Player player = new Player("A Player");

		long playerId = dataManager.savePlayer(player);

		dataManager.deletePlayer(player);

		assertEquals(0, player.getId());
		assertNull(playerDao.get(playerId));
	}
}
