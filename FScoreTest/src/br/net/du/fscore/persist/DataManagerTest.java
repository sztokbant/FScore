package br.net.du.fscore.persist;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.MatchPlayerKey;
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

public class DataManagerTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	MatchDAO matchDao;
	PlayerDAO playerDao;
	MatchPlayerDAO matchPlayerDao;
	RoundDAO roundDao;
	PlayerRoundDAO playerRoundDao;

	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		clearAllTables();

		matchDao = new MatchDAO(db);
		playerDao = new PlayerDAO(db);
		matchPlayerDao = new MatchPlayerDAO(db);
		roundDao = new RoundDAO(db);
		playerRoundDao = new PlayerRoundDAO(db);
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
		PlayerRoundTable.clear(db);
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

		Player player1 = new Player("A Player");
		Player player2 = new Player("Player 2");

		match.withPlayer(player1);
		match.withPlayer(player2);

		Round round1 = new Round(3);
		PlayerRound pr11 = new PlayerRound(player1);
		round1.addPlayerRound(pr11);
		PlayerRound pr12 = new PlayerRound(player2);
		round1.addPlayerRound(pr12);

		Round round2 = new Round(7);
		PlayerRound pr21 = new PlayerRound(player1);
		round2.addPlayerRound(pr21);
		PlayerRound pr22 = new PlayerRound(player2);
		round2.addPlayerRound(pr22);

		match.addRound(round1);
		match.addRound(round2);

		long matchId = dataManager.saveMatch(match);

		// match
		assertTrue(matchId > 0);
		assertEquals(matchId, match.getId());

		Match match2 = matchDao.retrieve(matchId);

		// players
		assertTrue(player1.isPersistent());
		assertTrue(player2.isPersistent());
		match2.withPlayer(playerDao.retrieve(player1.getId()));
		match2.withPlayer(playerDao.retrieve(player2.getId()));

		// rounds
		assertTrue(round1.isPersistent());
		assertTrue(round2.isPersistent());
		assertTrue(round1.getId() != round2.getId());
		assertEquals(matchId, round1.getMatchId());
		assertEquals(matchId, round2.getMatchId());

		// player rounds
		assertEquals(round1.getId(), pr11.getRoundId());
		assertEquals(round1.getId(), pr12.getRoundId());
		assertEquals(round2.getId(), pr21.getRoundId());
		assertEquals(round2.getId(), pr22.getRoundId());

		Round round3 = roundDao.retrieve(round1.getId());
		Round round4 = roundDao.retrieve(round2.getId());
		round3.addPlayerRound(playerRoundDao.retrieve(1));
		round3.addPlayerRound(playerRoundDao.retrieve(2));
		round4.addPlayerRound(playerRoundDao.retrieve(3));
		round4.addPlayerRound(playerRoundDao.retrieve(4));
		match2.addRound(round3);
		match2.addRound(round4);

		MatchPlayerKey key1 = new MatchPlayerKey(matchId, player1.getId());
		MatchPlayerKey key2 = new MatchPlayerKey(matchId, player2.getId());

		// these are the tests that really matter, all the others are
		// emphasizing them
		assertTrue(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));
		assertEquals(match, match2);
	}

	public void testSaveAnExistingMatchAfterAddingAPlayer() {
		Match match = new Match("Match Name");
		Player player1 = new Player("A Player");
		Player player2 = new Player("A Second Player");

		// first save
		long matchId = dataManager.saveMatch(match);

		// add a Player and save
		match.withPlayer(player1);
		dataManager.saveMatch(match);

		// add another Player and save
		match.withPlayer(player2);
		dataManager.saveMatch(match);

		long player1Id = player1.getId();
		long player2Id = player2.getId();

		MatchPlayerKey key1 = new MatchPlayerKey(matchId, player1Id);
		MatchPlayerKey key2 = new MatchPlayerKey(matchId, player2Id);

		assertTrue(player1.isPersistent());
		assertTrue(player2.isPersistent());
		assertTrue(player2.getId() > player1.getId());
		assertEquals(2, match.getPlayers().size());
		assertEquals(match.getPlayers().get(0), playerDao.retrieve(player1Id));
		assertEquals(match.getPlayers().get(1), playerDao.retrieve(player2Id));

		// these are the tests that really matter, all the others are
		// emphasizing them
		assertTrue(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));
		assertEquals(match, dataManager.retrieveMatch(matchId));
	}

	public void testSaveAnExistingMatchAfterRemovingAPlayer() {
		Match match = new Match("Match Name");
		Player player1 = new Player("A Player");
		Player player2 = new Player("A Second Player");
		match.withPlayer(player1);
		match.withPlayer(player2);

		// first save
		long matchId = dataManager.saveMatch(match);

		long player1Id = player1.getId();
		long player2Id = player2.getId();

		MatchPlayerKey key1 = new MatchPlayerKey(matchId, player1Id);
		MatchPlayerKey key2 = new MatchPlayerKey(matchId, player2Id);

		// delete a Player and save
		match.getPlayers().remove(player1);
		dataManager.saveMatch(match);

		assertEquals(matchId, match.getId());

		// this will fail for the Id is updated on a copy of the object which is
		// not in the list anymore
		// assertEquals(0, player1.getId());

		assertEquals(player2Id, player2.getId());
		assertNull(playerDao.retrieve(player1Id));
		assertEquals(match.getPlayers().get(0), playerDao.retrieve(player2Id));

		// these are the tests that really matter, all the others are
		// emphasizing them
		assertFalse(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));
		assertEquals(match, dataManager.retrieveMatch(matchId));
	}

	public void testSaveAnExistingMatchAfterAddingARound() {
		Match match1 = new Match("Match Name");
		Player player = new Player("A Player");
		match1.withPlayer(player);

		// first save
		dataManager.saveMatch(match1);

		// add an empty round and save
		Round round1 = new Round(7);
		match1.addRound(round1);
		dataManager.saveMatch(match1);

		// add a round with a playerround and save
		Round round2 = new Round(3);
		PlayerRound playerRound = new PlayerRound(player);
		playerRound.setBet(3);
		round2.addPlayerRound(playerRound);
		match1.addRound(round2);
		dataManager.saveMatch(match1);

		// update a playerround and save
		playerRound.setWins(2);
		dataManager.saveMatch(match1);

		Match match2 = dataManager.retrieveMatch(match1.getId());

		assertTrue(round1.isPersistent());
		assertTrue(round2.isPersistent());
		assertTrue(round2.getId() > round1.getId());
		assertEquals(2, match1.getRounds().size());
		assertEquals(2, match2.getRounds().size());
		assertEquals(match1.getRounds().get(1).getPlayerRounds().size(), match2
				.getRounds().get(1).getPlayerRounds().size());
		assertEquals(match1.getRounds().get(1).getPlayerRounds().get(0)
				.getBet(), match2.getRounds().get(1).getPlayerRounds().get(0)
				.getBet());
		assertEquals(match1.getRounds().get(1).getPlayerRounds().get(0)
				.getWins(), match2.getRounds().get(1).getPlayerRounds().get(0)
				.getWins());
		assertEquals(match1.getRounds().get(1).getPlayerRounds(), match2
				.getRounds().get(1).getPlayerRounds());

		// these is the test that really matters, all the others are
		// emphasizing them
		assertEquals(match1, match2);
	}

	public void testSaveAnExistingMatchAfterRemovingARound() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		Round round1 = new Round(3);
		round1.addPlayerRound(new PlayerRound(player));
		Round round2 = new Round(7);
		round2.addPlayerRound(new PlayerRound(player));

		match.addRound(round1);
		match.addRound(round2);

		// first save
		long matchId = dataManager.saveMatch(match);

		long round1Id = round1.getId();
		long round2Id = round2.getId();

		// remove a Round and save
		match.getRounds().remove(round1);
		dataManager.saveMatch(match);

		assertEquals(matchId, match.getId());
		assertNull(roundDao.retrieve(round1Id));
		assertEquals(match, dataManager.retrieveMatch(matchId));

		// remove another Round and save
		match.getRounds().remove(round2);
		dataManager.saveMatch(match);

		assertEquals(matchId, match.getId());
		assertNull(roundDao.retrieve(round2Id));
		assertEquals(match, dataManager.retrieveMatch(matchId));
	}

	public void testSaveAnExistingMatchAfterUpdatingARound() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		Player player2 = new Player("Another Player");
		match.withPlayer(player).withPlayer(player2);

		Round round1 = new Round(7);
		PlayerRound playerRound1 = new PlayerRound(player);
		playerRound1.setBet(5);
		round1.addPlayerRound(playerRound1);

		match.addRound(round1);

		// first save
		dataManager.saveMatch(match);
		assertEquals(match, dataManager.retrieveMatch(match.getId()));

		// add a new PlayerRound and save
		PlayerRound playerRound2 = new PlayerRound(player2);
		playerRound2.setBet(3);
		round1.addPlayerRound(playerRound2);
		dataManager.saveMatch(match);
		assertEquals(match, dataManager.retrieveMatch(match.getId()));

		// update a PlayerRound and save
		playerRound1.setWins(6);
		playerRound2.setWins(1);
		dataManager.saveMatch(match);
		assertEquals(match, dataManager.retrieveMatch(match.getId()));

		// delete a PlayerRound and save
		round1.getPlayerRounds().remove(0);
		dataManager.saveMatch(match);
		assertEquals(match, dataManager.retrieveMatch(match.getId()));
	}

	public void testRetrieveMatch() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		Round round1 = new Round(7);
		round1.addPlayerRound(new PlayerRound(player));
		match.addRound(round1);

		dataManager.saveMatch(match);

		Match match2 = dataManager.retrieveMatch(match.getId());

		assertEquals(match, match2);
	}

	public void testRetrieveAllMatches() {
		Match match = new Match("Match Name");
		Player player = new Player("A Player");
		match.withPlayer(player);

		Round round = new Round(7);
		round.addPlayerRound(new PlayerRound(player));
		match.addRound(round);

		List<Match> matches = new ArrayList<Match>();
		matches.add(match);

		dataManager.saveMatch(match);

		assertEquals(matches, dataManager.retrieveAllMatches());
	}

	public void testDeleteMatch() {
		Match match = new Match("Match Name");

		Player player = new Player("A Player");
		match.withPlayer(player);

		PlayerRound playerRound = new PlayerRound(player);
		Round round = new Round(7);
		round.addPlayerRound(playerRound);
		match.addRound(round);

		long matchId = dataManager.saveMatch(match);

		long playerId = player.getId();
		MatchPlayerKey key = new MatchPlayerKey(matchId, playerId);
		long roundId = round.getId();
		long playerRoundId = playerRound.getId();

		assertTrue(match.isPersistent());
		assertTrue(player.isPersistent());
		assertTrue(round.isPersistent());
		assertTrue(playerRound.isPersistent());

		dataManager.deleteMatch(match);

		assertFalse(match.isPersistent());
		assertFalse(player.isPersistent());
		assertFalse(round.isPersistent());
		assertFalse(playerRound.isPersistent());

		assertNull(matchDao.retrieve(matchId));
		assertNull(playerDao.retrieve(playerId));
		assertNull(roundDao.retrieve(roundId));
		assertFalse(matchPlayerDao.exists(key));
		assertNull(playerRoundDao.retrieve(playerRoundId));
	}

	public void testDeletingAMatchWontDeleteNonOrphanPlayers() {
		// initial state
		Match match1 = new Match("Match One");
		Match match2 = new Match("Match Two");
		Player player = new Player("A Player");
		match1.withPlayer(player);
		match2.withPlayer(player);

		// save both
		long matchId1 = dataManager.saveMatch(match1);
		long matchId2 = dataManager.saveMatch(match2);

		long playerId = player.getId();

		MatchPlayerKey key1 = new MatchPlayerKey(matchId1, playerId);
		MatchPlayerKey key2 = new MatchPlayerKey(matchId2, playerId);

		dataManager.deleteMatch(match1);

		assertFalse(match1.isPersistent());
		assertTrue(match2.isPersistent());
		assertEquals(playerId, player.getId());
		assertNull(matchDao.retrieve(matchId1));
		assertEquals(match2, dataManager.retrieveMatch(matchId2));
		assertEquals(player, playerDao.retrieve(playerId));
		assertFalse(matchPlayerDao.exists(key1));
		assertTrue(matchPlayerDao.exists(key2));
	}

	public void testRetrievePlayers() {
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

		assertEquals(players, dataManager.retrievePlayers(key.getMatchId()));

		PlayerTable.clear(db);
	}

	public void testRetrieveRoundById() {
		Player player = new Player("My Player");
		playerDao.save(player);

		Round round1 = new Round(19);
		round1.addPlayerRound(new PlayerRound(player));

		roundDao.save(round1);
		for (PlayerRound playerRound : round1.getPlayerRounds()) {
			playerRound.setRoundId(round1.getId());
			playerRoundDao.save(playerRound);
		}

		Round round2 = dataManager.retrieveRoundById(round1.getId());

		assertEquals(round1.getPlayerRounds().size(), round2.getPlayerRounds()
				.size());
		assertEquals(round1, round2);
	}
}