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

	public void testSaveAnExistingMatchAfterAddingAPlayer() {
		Match match = new Match("Match Name");
		Player player1 = new Player("A Player");
		Player player2 = new Player("A Second Player");

		match.withPlayer(player1);
		long matchId = dataManager.saveMatch(match);
		match.withPlayer(player2);
		dataManager.saveMatch(match);

		long player1Id = player1.getId();
		long player2Id = player2.getId();

		MatchPlayerKey key1 = new MatchPlayerKey(matchId, player1Id);
		MatchPlayerKey key2 = new MatchPlayerKey(matchId, player2Id);

		assertTrue(player1.getId() > 0);
		assertTrue(player2.getId() > 0);
		assertEquals(2, match.getPlayers().size());
		assertEquals(match.getPlayers().get(0), playerDao.get(player1Id));
		assertEquals(match.getPlayers().get(1), playerDao.get(player2Id));
		assertTrue(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));
		assertEquals(match, dataManager.getMatch(matchId));
	}

	public void testSaveAnExistingMatchAfterRemovingAPlayer() {
		Match match = new Match("Match Name");
		Player player1 = new Player("A Player");
		Player player2 = new Player("A Second Player");
		match.withPlayer(player1);
		match.withPlayer(player2);

		long matchId = dataManager.saveMatch(match);
		long player1Id = player1.getId();
		long player2Id = player2.getId();

		MatchPlayerKey key1 = new MatchPlayerKey(matchId, player1Id);
		MatchPlayerKey key2 = new MatchPlayerKey(matchId, player2Id);

		match.getPlayers().remove(player1);

		dataManager.saveMatch(match);

		assertEquals(matchId, match.getId());

		// this will fail for the Id is updated on a copy of the object which is
		// not in the list anymore
		// assertEquals(0, player1.getId());

		assertEquals(player2Id, player2.getId());
		assertNull(playerDao.get(player1Id));
		assertEquals(match.getPlayers().get(0), playerDao.get(player2Id));
		assertFalse(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));

		assertEquals(match, dataManager.getMatch(matchId));
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

	public void testDeletingAMatchWontDeleteNonOrphanPlayers() {
		Match match1 = new Match("Match One");
		Match match2 = new Match("Match Two");
		Player player = new Player("A Player");
		match1.withPlayer(player);
		match2.withPlayer(player);

		long matchId1 = dataManager.saveMatch(match1);
		long matchId2 = dataManager.saveMatch(match2);
		long playerId = player.getId();

		MatchPlayerKey key1 = new MatchPlayerKey(matchId1, playerId);
		MatchPlayerKey key2 = new MatchPlayerKey(matchId2, playerId);

		dataManager.deleteMatch(match1);

		assertEquals(0, match1.getId());
		assertEquals(playerId, player.getId());
		assertNull(matchDao.get(matchId1));
		assertEquals(match2, dataManager.getMatch(matchId2));
		assertEquals(player, playerDao.get(playerId));
		assertFalse(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));
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
}
