package br.net.du.fscore.test.persist;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.MatchPlayerKey;
import br.net.du.fscore.persist.dao.MatchDAO;
import br.net.du.fscore.persist.dao.MatchPlayerDAO;
import br.net.du.fscore.persist.dao.PlayerDAO;
import br.net.du.fscore.persist.dao.RoundDAO;
import br.net.du.fscore.persist.table.MatchPlayerTable;
import br.net.du.fscore.persist.table.MatchTable;
import br.net.du.fscore.persist.table.PlayerTable;
import br.net.du.fscore.persist.table.RoundTable;

public class DataManagerTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	MatchDAO matchDao;
	PlayerDAO playerDao;
	MatchPlayerDAO matchPlayerDao;
	RoundDAO roundDao;

	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		clearAllTables();

		matchDao = new MatchDAO(db);
		playerDao = new PlayerDAO(db);
		matchPlayerDao = new MatchPlayerDAO(db);
		roundDao = new RoundDAO(db);
	}

	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void clearAllTables() {
		MatchPlayerTable.clear(db);
		MatchTable.clear(db);
		PlayerTable.clear(db);
		RoundTable.clear(db);
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
		Player player2 = new Player("Player 2");
		match.withPlayer(player);
		match.withPlayer(player2);

		Round round1 = new Round(3);
		// round1.addPlayerRound(new PlayerRound(player));
		// round1.addPlayerRound(new PlayerRound(player2));

		Round round2 = new Round(7);
		// round2.addPlayerRound(new PlayerRound(player));
		// round2.addPlayerRound(new PlayerRound(player2));

		match.addRound(round1);
		match.addRound(round2);

		Log.i(getContext().getResources().getString(R.string.app_name),
				"beginning DataManager.saveMatch()");
		long matchId = dataManager.saveMatch(match);
		Log.i(getContext().getResources().getString(R.string.app_name),
				"finishing DataManager.saveMatch()");

		assertTrue(matchId > 0);
		assertEquals(matchId, match.getId());
		assertTrue(player.getId() > 0);
		assertTrue(round1.getId() > 0);
		assertTrue(round2.getId() > 0);
		assertTrue(round1.getId() != round2.getId());
		assertEquals(matchId, round1.getMatchId());
		assertEquals(matchId, round2.getMatchId());

		Match match2 = matchDao.get(matchId);

		// build Match from scratch using DAOs to verify equivalence
		match2.withPlayer(playerDao.get(player.getId()));
		match2.withPlayer(playerDao.get(player2.getId()));
		match2.addRound(roundDao.get(round1.getId()));
		match2.addRound(roundDao.get(round2.getId()));

		// TODO: add PlayerRounds otherwise the next test will fail
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
		assertTrue(player2.getId() > player1.getId());
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

	public void testSaveAnExistingMatchAfterAddingARound() {
		Match match = new Match("Match Name");
		Round round1 = new Round(7);
		Round round2 = new Round(3);

		match.addRound(round1);
		long matchId = dataManager.saveMatch(match);
		match.addRound(round2);
		dataManager.saveMatch(match);

		long round1Id = round1.getId();
		long round2Id = round2.getId();

		assertTrue(round1.getId() > 0);
		assertTrue(round2.getId() > 0);
		assertTrue(round2.getId() > round1.getId());
		assertEquals(2, match.getRounds().size());
		assertEquals(match.getRounds().get(0), roundDao.get(round1Id));
		assertEquals(match.getRounds().get(1), roundDao.get(round2Id));

		assertEquals(match, dataManager.getMatch(matchId));
	}

	public void testSaveAnExistingMatchAfterRemovingARound() {
		Match match = new Match("Match Name");
		Round round1 = new Round(3);
		Round round2 = new Round(7);
		match.addRound(round1);
		match.addRound(round2);

		long matchId = dataManager.saveMatch(match);
		long round1Id = round1.getId();
		long round2Id = round2.getId();

		match.getRounds().remove(round1);

		dataManager.saveMatch(match);

		assertEquals(matchId, match.getId());

		// this will fail for the Id is updated on a copy of the object which is
		// not in the list anymore
		// assertEquals(0, round1.getId());

		assertEquals(round2Id, round2.getId());
		assertNull(roundDao.get(round1Id));
		assertEquals(match.getRounds().get(0), roundDao.get(round2Id));

		assertEquals(match, dataManager.getMatch(matchId));
	}

	public void testGetMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);
		match.addRound(new Round(7));

		dataManager.saveMatch(match);

		assertEquals(match, dataManager.getMatch(match.getId()));
	}

	public void testGetAllMatches() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);
		match.addRound(new Round(7));

		List<Match> matches = new ArrayList<Match>();
		matches.add(match);

		dataManager.saveMatch(match);

		assertEquals(matches, dataManager.getAllMatches());
	}

	public void testDeleteMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);
		Round round = new Round(7);

		long matchId = dataManager.saveMatch(match);
		long playerId = player.getId();
		MatchPlayerKey key = new MatchPlayerKey(matchId, playerId);
		long roundId = round.getId();

		dataManager.deleteMatch(match);

		assertEquals(0, match.getId());
		assertEquals(0, player.getId());
		assertEquals(0, round.getId());
		assertNull(matchDao.get(matchId));
		assertNull(playerDao.get(playerId));
		assertNull(roundDao.get(roundId));
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

	public void testGetPlayers() {
		PlayerTable.clear(db);

		MatchPlayerDAO matchPlayerDao = new MatchPlayerDAO(db);
		MatchPlayerKey key = new MatchPlayerKey(7, 11);

		List<Player> players = new ArrayList<Player>();
		players.add(new Player("Dummy 1"));
		players.add(new Player("Dummy 2"));
		players.add(new Player("Dummy 3"));

		PlayerDAO playerDao = new PlayerDAO(db);
		for (Player p : players) {
			playerDao.save(p);
			key.setPlayerId(p.getId());
			matchPlayerDao.save(key);
		}

		assertEquals(players, dataManager.getPlayers(key.getMatchId()));

		PlayerTable.clear(db);
	}
}
