package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
		menu.setHeaderTitle(selectedPlayerRound.getPlayer().toString());
		MenuItem bet = menu.add(0, 0, 0, "Bet");
		bet.setOnMenuItemClickListener(playerRoundDeleteClickListener());
		MenuItem wins = menu.add(0, 1, 0, "Wins");
		wins.setOnMenuItemClickListener(playerRoundDeleteClickListener());
	}

	private AlertDialog.Builder getBetDialog() {
		final EditText betInput = new EditText(SingleRound.this);
		betInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		return new AlertDialog.Builder(SingleRound.this)
				.setTitle(selectedPlayerRound.getPlayer().toString())
				.setMessage("Bet").setView(betInput)
				.setPositiveButton("Ok", getDoMakeBetClick(betInput))
				.setNegativeButton("Cancel", null);
	}

	private OnClickListener getDoMakeBetClick(final EditText betInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				Editable value = betInput.getText();

				try {
					long bet = Long.parseLong(value.toString());

					round.setBet(selectedPlayerRound.getPlayer(), bet);
					dataManager.saveMatch(match);
					refreshPlayerRoundsList();
				} catch (NumberFormatException e) {
					Toast.makeText(
							SingleRound.this,
							"Please enter a number between 0 and "
									+ round.getNumberOfCards(),
							Toast.LENGTH_SHORT).show();
				} catch (IllegalArgumentException e) {
					Toast.makeText(SingleRound.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private AlertDialog.Builder getWinsDialog() {
		final EditText winsInput = new EditText(SingleRound.this);
		winsInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		return new AlertDialog.Builder(SingleRound.this)
				.setTitle(selectedPlayerRound.getPlayer().toString())
				.setMessage("Wins").setView(winsInput)
				.setPositiveButton("Ok", getDoMakeWinsClick(winsInput))
				.setNegativeButton("Cancel", null);
	}

	private OnClickListener getDoMakeWinsClick(final EditText winsInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				Editable value = winsInput.getText();

				try {
					long wins = Long.parseLong(value.toString());

					round.setWins(selectedPlayerRound.getPlayer(), wins);
					dataManager.saveMatch(match);
					refreshPlayerRoundsList();

				} catch (NumberFormatException e) {
					Toast.makeText(
							SingleRound.this,
							"Please enter a number between 0 and "
									+ round.getNumberOfCards(),
							Toast.LENGTH_SHORT).show();
				} catch (IllegalArgumentException e) {
					Toast.makeText(SingleRound.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private OnMenuItemClickListener playerRoundDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == 0) {
					getBetDialog().show();
				} else if (item.getItemId() == 1) {
					getWinsDialog().show();
				}

				return true;
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

		playerRoundsView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				List<PlayerRound> playerRounds = round.getPlayerRounds();
				Collections.sort(playerRounds);
				selectedPlayerRound = playerRounds.get(position);

				if (selectedPlayerRound.getBet() == PlayerRound.EMPTY) {
					getBetDialog().show();
				} else {
					getWinsDialog().show();
				}
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
