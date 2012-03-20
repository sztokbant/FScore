package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.adapters.PlayerRoundsAdapter;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.model.exceptions.FScoreException;
import br.net.du.fscore.persist.DataManager;

public abstract class SingleRound extends Activity {
	private ArrayAdapter<PlayerRound> playerRoundAdapter;
	private List<PlayerRound> playerRounds = new ArrayList<PlayerRound>();
	PlayerRound selectedPlayerRound;

	Match match;
	private long matchId;

	protected Round round;
	private long roundId;

	DataManager dataManager;

	protected StringBuilder windowTitle;

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

		windowTitle = new StringBuilder();

		windowTitle.append(getString(R.string.round) + " - "
				+ round.getNumberOfCards() + " ");
		windowTitle
				.append((round.getNumberOfCards() == 1) ? getString(R.string.card)
						: getString(R.string.cards));
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataManager.closeDb();
	}

	protected abstract AlertDialog.Builder getInputDialog()
			throws IllegalStateException;

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

				try {
					getInputDialog().show();
				} catch (IllegalStateException e) {
					new ActivityUtils().showErrorDialog(SingleRound.this,
							e.getMessage());
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

	void refreshPlayerRoundsList() throws FScoreException {
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
