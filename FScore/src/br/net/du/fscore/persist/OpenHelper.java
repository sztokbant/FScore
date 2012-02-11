package br.net.du.fscore.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.net.du.fscore.persist.table.MatchPlayerTable;
import br.net.du.fscore.persist.table.MatchTable;
import br.net.du.fscore.persist.table.PlayerRoundTable;
import br.net.du.fscore.persist.table.PlayerTable;
import br.net.du.fscore.persist.table.RoundTable;

class OpenHelper extends SQLiteOpenHelper {
	private Context context;

	OpenHelper(final Context context, boolean useDebugDatabase) {
		super(context, useDebugDatabase ? DataConstants.DEBUG_DATABASE_NAME
				: DataConstants.DATABASE_NAME, null,
				DataManager.DATABASE_VERSION);
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
