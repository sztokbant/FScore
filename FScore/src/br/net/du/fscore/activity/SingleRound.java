package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
		menu.setHeaderTitle(selectedPlayerRound.getPlayer().toString());
		MenuItem bet = menu.add(0, 0, 0, "Bet");
		bet.setOnMenuItemClickListener(playerRoundDeleteClickListener());
		MenuItem wins = menu.add(0, 1, 0, "Wins");
		wins.setOnMenuItemClickListener(playerRoundDeleteClickListener());
	}

	private OnMenuItemClickListener playerRoundDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == 0) {
					// bet
					final EditText betInput = new EditText(SingleRound.this);
					betInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

					new AlertDialog.Builder(SingleRound.this)
							.setTitle(
									selectedPlayerRound.getPlayer().toString())
							.setMessage("Bet")
							.setView(betInput)
							.setPositiveButton("Ok",
									getDoMakeBetClick(betInput))
							.setNegativeButton("Cancel", null).show();
				} else if (item.getItemId() == 1) {
					// wins
					final EditText winsInput = new EditText(SingleRound.this);
					winsInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

					new AlertDialog.Builder(SingleRound.this)
							.setTitle(
									selectedPlayerRound.getPlayer().toString())
							.setMessage("Wins")
							.setView(winsInput)
							.setPositiveButton("Ok",
									getDoMakeWinsClick(winsInput))
							.setNegativeButton("Cancel", null).show();
				}

				return true;
			}

			private OnClickListener getDoMakeBetClick(EditText betInput) {
				Editable value = betInput.getText();
				return null;
			}

			private OnClickListener getDoMakeWinsClick(EditText winsInput) {
				Editable value = winsInput.getText();
				return null;
			}
		};
	}

	private void createPlayerRoundsListAdapter() {
		final ListView playerRoundsView = (ListView) findViewById(R.id_singleround.playerroundlist);
		playerRoundAdapter = new ArrayAdapter<PlayerRound>(this,
				android.R.layout.simple_list_item_1, playerRounds);
		playerRoundsView.setAdapter(playerRoundAdapter);

		playerRoundsView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						// for context menu title and deleting
						List<PlayerRound> playerRounds = round
								.getPlayerRounds();
						Collections.sort(playerRounds);
						selectedPlayerRound = playerRounds.get(position);

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
