package br.net.du.fscore.activity;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
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
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;

public class SingleRound extends Activity {
	private ArrayAdapter<PlayerRound> playerRoundAdapter;
	private Round round;
	private List<PlayerRound> playerRounds;
	private Match match;
	private DataManager dataManager;
	private PlayerRound selectedPlayerRound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleround);

		dataManager = new DataManager(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dataManager.openDb();

		long matchId = (Long) getIntent().getSerializableExtra("matchId");
		long selectedRoundId = (Long) getIntent().getSerializableExtra(
				"selectedRoundId");
		match = dataManager.retrieveMatch(matchId);
		for (Round r : match.getRounds()) {
			if (r.getId() == selectedRoundId) {
				round = r;
				break;
			}
		}

		playerRounds = round.getPlayerRounds();

		final ListView playerRoundsList = (ListView) findViewById(R.id_singleround.playerroundlist);
		playerRoundAdapter = new ArrayAdapter<PlayerRound>(this,
				android.R.layout.simple_list_item_1, playerRounds);
		playerRoundsList.setAdapter(playerRoundAdapter);

		playerRoundsList.setOnItemClickListener(new OnItemClickListener() {
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

		playerRoundsList
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

		registerForContextMenu(playerRoundsList);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuItem newPlayerRound =
		menu.add(0, 0, 0, "New Player Entry");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			List<Player> players = match.getPlayers();

			if (players.size() == 0) {
				Toast.makeText(SingleRound.this, "No players in this match",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			// TODO this PlayerRound is dummy
			Player rndPlayer = players
					.get(new Random().nextInt(players.size()));
			PlayerRound playerRound = new PlayerRound(rndPlayer);
			playerRound.setBet(new Random().nextInt(7) + 1);
			playerRound
					.setBet(new Random().nextInt((int) playerRound.getBet()) + 1);
			if (rndPlayer.isPersistent()) {
				Log.i("FScore", "player IS persistent");
			} else {
				Log.i("FScore", "player IS NOT persistent");
			}

			playerRounds.add(playerRound);
			dataManager.saveMatch(match);
			playerRoundAdapter.notifyDataSetChanged();
		}

		return false;
	}

	private OnMenuItemClickListener playerRoundDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleRound.this)
						.setTitle("Delete" + selectedPlayerRound)
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(
										SingleRound.this,
										"Deleting " + selectedPlayerRound
												+ "...", Toast.LENGTH_SHORT)
										.show();

								playerRounds.remove(selectedPlayerRound);
								dataManager.saveRound(round);
								playerRoundAdapter.notifyDataSetChanged();
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		};
	}
}
