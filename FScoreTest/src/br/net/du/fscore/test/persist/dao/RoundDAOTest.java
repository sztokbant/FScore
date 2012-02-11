package br.net.du.fscore.test.persist.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;
import br.net.du.fscore.persist.dao.RoundDAO;
import br.net.du.fscore.persist.table.RoundTable;
import br.net.du.fscore.persist.table.RoundTable.RoundColumns;

public class RoundDAOTest extends AndroidTestCase {
	SQLiteDatabase db;
	DataManager dataManager;

	RoundDAO dao;
	Round round;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataManager = new DataManager(getContext(), true);
		db = dataManager.getDb();
		dataManager.openDb();

		RoundTable.clear(db);
		dao = new RoundDAO(db);

		round = new Round(3);
		dao.save(round);
	}

	@Override
	protected void tearDown() throws Exception {
		dataManager.closeDb();
		super.tearDown();
	}

	public void testSaveNew() {
		Cursor cursor = db.query(RoundTable.NAME, RoundColumns.get(),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(round.getId()) }, null, null,
				null, null);

		// asserts round was saved properly
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(1, cursor.getLong(0));
		assertEquals(3, cursor.getLong(1));
		assertEquals(round.getMatchId(), cursor.getLong(2));
		assertEquals(1, round.getId());

		cursor.close();
	}

	public void testSaveExisting() {
		round.setNumberOfCards(7);
		dao.save(round);

		Cursor cursor = db.query(RoundTable.NAME, RoundColumns.get(),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(round.getId()) }, null, null,
				null, null);

		// asserts round was updated properly
		assertEquals(1, cursor.getCount());
		assertTrue(cursor.moveToNext());
		assertEquals(1, cursor.getLong(0));
		assertEquals(7, cursor.getLong(1));
		assertEquals(round.getMatchId(), cursor.getLong(2));
		assertEquals(1, round.getId());

		cursor.close();
	}

	public void testDelete() {
		dao.delete(round);

		Cursor cursor = db.query(RoundTable.NAME, RoundColumns.get(),
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(round.getId()) }, null, null,
				null, null);

		// asserts round was deleted properly
		assertEquals(0, cursor.getCount());
		assertFalse(cursor.moveToNext());
		assertEquals(0, round.getId());

		cursor.close();
	}

	public void testGet() {
		Round round2 = dao.retrieve(round.getId());
		assertEquals(round, round2);
	}

	public void testGetRoundsForMatch() {
		long matchId = 19;

		List<Round> rounds = new ArrayList<Round>();
		rounds.add(new Round(2));
		rounds.add(new Round(3));
		rounds.add(new Round(7));

		for (Round r : rounds) {
			r.setMatchId(matchId);
			dao.save(r);
		}

		assertEquals(rounds, dao.retrieveRoundsForMatch(matchId));
	}
}
