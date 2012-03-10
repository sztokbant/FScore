package br.net.du.fscore.persist.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.exceptions.FScoreException;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.dao.PlayerDAO;
import br.net.du.fscore.persist.dao.PlayerRoundDAO;
import br.net.du.fscore.persist.table.PlayerRoundTable;
import br.net.du.fscore.persist.table.PlayerTable;

public class PlayerRoundDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	PlayerRoundDAO dao;
	PlayerDAO playerDao;

	PlayerRound playerRound;
	Player player;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();

		PlayerRoundTable.clear(db);
		PlayerTable.clear(db);

		dao = new PlayerRoundDAO(db);
		playerDao = new PlayerDAO(db);

		player = new Player("My Player");
		playerDao.save(player);

		playerRound = new PlayerRound(player);
		playerRound.setBet(7);
		playerRound.setWins(14);
		playerRound.setRoundId(19);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testSaveNew() {
		dao.save(playerRound);

		Cursor cursor = db.query(PlayerRoundTable.NAME, null, null, null, null,
				null, null);

		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertTrue(playerRound.getId() > 0);
		assertEquals(playerRound.getId(), cursor.getLong(0));
		assertEquals(playerRound.getRoundId(), cursor.getLong(1));
		assertEquals(playerRound.getPlayer().getId(), cursor.getLong(2));
		assertEquals(playerRound.getBet(), cursor.getLong(3));
		assertEquals(playerRound.getWins(), cursor.getLong(4));

		cursor.close();
	}

	public void testSaveExisting() throws FScoreException {
		dao.save(playerRound);

		Player player2 = new Player("Other");
		playerDao.save(player2);

		playerRound.setPlayer(player2);
		playerRound.setBet(14);
		playerRound.setWins(28);
		playerRound.setRoundId(38);

		dao.save(playerRound);

		Cursor cursor = db.query(PlayerRoundTable.NAME, null, null, null, null,
				null, null);

		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertTrue(playerRound.getId() > 0);
		assertEquals(playerRound.getId(), cursor.getLong(0));
		assertEquals(playerRound.getRoundId(), cursor.getLong(1));
		assertEquals(playerRound.getPlayer().getId(), cursor.getLong(2));
		assertEquals(playerRound.getBet(), cursor.getLong(3));
		assertEquals(playerRound.getWins(), cursor.getLong(4));

		cursor.close();
	}

	public void testDelete() {
		dao.save(playerRound);
		dao.delete(playerRound);

		Cursor cursor = db.query(PlayerRoundTable.NAME, null, null, null, null,
				null, null);

		// asserts playerRound was deleted properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());

		cursor.close();
	}

	public void testExists() {
		dao.save(playerRound);
		assertTrue(dao.exists(playerRound));
		dao.delete(playerRound);
		assertFalse(dao.exists(playerRound));
	}

	public void testRetrieve() throws FScoreException {
		dao.save(playerRound);
		PlayerRound playerRound2 = dao.retrieve(playerRound.getId());
		assertEquals(playerRound, playerRound2);
	}

	public void testRetrievePlayerRoundsForRound() throws FScoreException {
		dao.save(playerRound);

		List<PlayerRound> playerRounds = new ArrayList<PlayerRound>();
		playerRounds.add(playerRound);

		// create and add a second PlayerRound object
		Player player2 = new Player("Dummy 2");
		playerDao.save(player2);

		PlayerRound playerRound2 = new PlayerRound(player2);
		playerRound2.setRoundId(playerRound.getRoundId());
		playerRound2.setBet(42);
		playerRound2.setWins(21);
		dao.save(playerRound2);
		playerRounds.add(playerRound2);

		assertEquals(playerRounds,
				dao.retrievePlayerRoundsForRound(playerRound.getRoundId()));

		PlayerTable.clear(db);
	}
}
