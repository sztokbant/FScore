package br.net.du.fscore.test.persist.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.DataManagerImpl;
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
		dataManager = new DataManagerImpl(getContext(), true);
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

	public void testSaveExisting() {
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
		assertEquals(playerRound.getRoundId(), cursor.getLong(0));
		assertEquals(playerRound.getPlayer().getId(), cursor.getLong(1));
		assertEquals(playerRound.getBet(), cursor.getLong(2));
		assertEquals(playerRound.getWins(), cursor.getLong(3));

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

	public void testGetPlayerRoundsForRound() {
		PlayerTable.clear(db);

		List<PlayerRound> playerRounds = new ArrayList<PlayerRound>();

		Player player1 = new Player("Dummy 1");
		Player player2 = new Player("Dummy 2");
		Player player3 = new Player("Dummy 3");

		playerRounds.add(new PlayerRound(player1));
		playerRounds.add(new PlayerRound(player2));
		playerRounds.add(new PlayerRound(player3));

		for (PlayerRound pr : playerRounds) {
			playerDao.save(pr.getPlayer());
			pr.setRoundId(7);
			dao.save(pr);
		}

		assertEquals(playerRounds,
				dao.getPlayerRoundsForMatch(playerRound.getRoundId()));

		PlayerTable.clear(db);
	}
}
