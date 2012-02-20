package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;

public class SingleRound extends Activity {
	private ArrayAdapter<PlayerRound> playerRoundAdapter;
	private List<PlayerRound> playerRounds = new ArrayList<PlayerRound>();
	private PlayerRound selectedPlayerRound;

	private Match match;
	private long matchId;

	private Round round;
	private long roundId;

	private DataManager dataManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleround);

		dataManager = new DataManager(this);

		matchId = (Long) getIntent().getSerializableExtra("matchId");
		roundId = (Long) getIntent().getSerializableExtra("selectedRoundId");

		createPlayerRoundsListAdapter();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dataManager.openDb();
		refreshPlayerRoundsList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataManager.closeDb();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(selectedPlayerRound.toString());
		MenuItem delete = menu.add(0, 0, 0, "Delete");
		delete.setOnMenuItemClickListener(playerRoundDeleteClickListener());
	}

	private OnMenuItemClickListener playerRoundDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleRound.this)
						.setTitle("Delete " + selectedPlayerRound)
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", getDoDeletePlayerRoundClick())
						.setNegativeButton("No", null).show();

				return true;
			}

			private OnClickListener getDoDeletePlayerRoundClick() {
				return new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(SingleRound.this,
								"Deleting " + selectedPlayerRound + "...",
								Toast.LENGTH_SHORT).show();

						int idx = match.getRounds().indexOf(round);
						match.getRounds().get(idx).getPlayerRounds()
								.remove(selectedPlayerRound);

						dataManager.saveMatch(match);
						refreshPlayerRoundsList();
					}
				};
			}
		};
	}

	private void createPlayerRoundsListAdapter() {
		final ListView playerRoundsView = (ListView) findViewById(R.id_singleround.playerroundlist);
		playerRoundAdapter = new ArrayAdapter<PlayerRound>(this,
				android.R.layout.simple_list_item_1, playerRounds);
		playerRoundsView.setAdapter(playerRoundAdapter);

		playerRoundsView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectedPlayerRound = (PlayerRound) playerRoundAdapter
						.getItem(position);
				Toast.makeText(SingleRound.this,
						"Opening PlayerRound " + selectedPlayerRound,
						Toast.LENGTH_SHORT).show();

				// Intent singlePlayerRound = new Intent(SingleRound.this,
				// SinglePlayerRound.class);
				// singlePlayerRound.putExtra("selectedPlayerRound",
				// selectedPlayerRound);
				// startActivity(singlePlayerRound);
			}
		});

		playerRoundsView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						// for context menu title and deleting
						selectedPlayerRound = round.getPlayerRounds().get(
								position);

						// won't consume the action
						return false;
					}
				});

		registerForContextMenu(playerRoundsView);
	}

	private void refreshPlayerRoundsList() {
		playerRounds.clear();

		match = dataManager.retrieveMatch(matchId);
		for (Round r : match.getRounds()) {
			if (r.getId() == roundId) {
				round = r;
				break;
			}
		}

		playerRounds.addAll(round.getPlayerRounds());
		Collections.sort(playerRounds);
		playerRoundAdapter.notifyDataSetChanged();
	}
}
