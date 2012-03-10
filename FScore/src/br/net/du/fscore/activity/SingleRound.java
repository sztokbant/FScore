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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.adapters.PlayerRoundsAdapter;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.model.exceptions.FScoreException;
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

		try {
			refreshPlayerRoundsList();
		} catch (FScoreException e) {
			new ActivityUtils().showErrorDialog(SingleRound.this,
					getString(e.getMessageId()));
		}

		String title = getString(R.string.round) + " - "
				+ round.getNumberOfCards() + " ";
		title += (round.getNumberOfCards() == 1) ? getString(R.string.card)
				: getString(R.string.cards);

		SingleRound.this.setTitle(title);
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
		MenuItem bet = menu.add(0, 0, 0, getString(R.string.bet));
		bet.setOnMenuItemClickListener(setBetOrWinsClickListener());
		MenuItem wins = menu.add(0, 1, 0, getString(R.string.wins));
		wins.setOnMenuItemClickListener(setBetOrWinsClickListener());
	}

	private AlertDialog.Builder getBetDialog() throws IllegalStateException {
		if (round.hasAnyWins()) {
			throw new IllegalStateException(
					getString(R.string.cannot_set_bet_after_wins));
		}

		final EditText betInput = new EditText(SingleRound.this);
		betInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		return new AlertDialog.Builder(SingleRound.this)
				.setTitle(selectedPlayerRound.getPlayer().toString())
				.setMessage(getString(R.string.bet))
				.setView(betInput)
				.setPositiveButton(getString(R.string.ok),
						getDoMakeBetClick(betInput))
				.setNegativeButton(getString(R.string.cancel), null);
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
					new ActivityUtils().showErrorDialog(SingleRound.this,
							getString(R.string.msg_enter_number_between_0_and)
									+ " " + round.getNumberOfCards() + ".");
				} catch (FScoreException e) {
					new ActivityUtils().showErrorDialog(SingleRound.this,
							getString(e.getMessageId()));
				}
			}
		};
	}

	private AlertDialog.Builder getWinsDialog() throws IllegalStateException {
		if (!round.hasAllBets()) {
			throw new IllegalStateException(
					getString(R.string.cannot_set_wins_before_all_bets));
		}

		final EditText winsInput = new EditText(SingleRound.this);
		winsInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		return new AlertDialog.Builder(SingleRound.this)
				.setTitle(selectedPlayerRound.getPlayer().toString())
				.setMessage(getString(R.string.wins))
				.setView(winsInput)
				.setPositiveButton(getString(R.string.ok),
						getDoMakeWinsClick(winsInput))
				.setNegativeButton(getString(R.string.cancel), null);
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
					new ActivityUtils().showErrorDialog(SingleRound.this,
							getString(R.string.msg_enter_number_between_0_and)
									+ " " + round.getNumberOfCards() + ".");
				} catch (FScoreException e) {
					new ActivityUtils().showErrorDialog(SingleRound.this,
							getString(e.getMessageId()));
				}
			}
		};
	}

	private OnMenuItemClickListener setBetOrWinsClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					if (item.getItemId() == 0) {
						getBetDialog().show();
					} else if (item.getItemId() == 1) {
						getWinsDialog().show();
					}
				} catch (IllegalStateException e) {
					new ActivityUtils().showErrorDialog(SingleRound.this,
							e.getMessage());
				}

				return true;
			}
		};
	}

	private void createPlayerRoundsListAdapter() {
		final ListView playerRoundsView = (ListView) findViewById(R.id_singleround.playerroundlist);
		playerRoundAdapter = new PlayerRoundsAdapter(this, 0, playerRounds);
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
					try {
						getBetDialog().show();
					} catch (IllegalStateException e) {
						new ActivityUtils().showErrorDialog(SingleRound.this,
								e.getMessage());
					}
				} else {
					try {
						getWinsDialog().show();
					} catch (IllegalStateException e) {
						new ActivityUtils().showErrorDialog(SingleRound.this,
								e.getMessage());
					}
				}
			}
		});

		registerForContextMenu(playerRoundsView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem resetWins = menu.add(0, 0, 0, getString(R.string.reset_wins));
		resetWins.setIcon(R.drawable.reset_wins);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			for (PlayerRound playerRound : round.getPlayerRounds()) {
				playerRound.setWins(PlayerRound.EMPTY);
			}

			try {
				dataManager.saveMatch(match);
				refreshPlayerRoundsList();
			} catch (FScoreException e) {
				new ActivityUtils().showErrorDialog(SingleRound.this,
						getString(e.getMessageId()));
			}
		}

		return false;
	}

	private void refreshPlayerRoundsList() throws FScoreException {
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
