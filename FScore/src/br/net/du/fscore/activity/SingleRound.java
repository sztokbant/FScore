package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout.LayoutParams;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.adapters.PlayerRoundsAdapter;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.model.exceptions.FScoreException;
import br.net.du.fscore.persist.DataManager;

public class SingleRound extends Activity {
	ArrayAdapter<PlayerRound> playerRoundAdapter;
	private List<PlayerRound> playerRounds = new ArrayList<PlayerRound>();
	PlayerRound selectedPlayerRound;
	ListView playerRoundsView;
	protected List<Button> buttons = new ArrayList<Button>();

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
		createButtons();
	}

	protected void createButtons() {
		buttons.clear();
		buttons.add(createDismissButton(getString(R.string.ok)));
		addButtonsToActivity();
	}

	protected void addButtonsToActivity() {
		LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id_singleround.btnlayout);

		for (Button button : buttons) {
			button.setGravity(Gravity.CENTER);
			buttonsLayout.addView(button, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		}
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

	protected void createPlayerRoundsListAdapter() {
		playerRoundsView = (ListView) findViewById(R.id_singleround.playerroundlist);
		playerRoundAdapter = new PlayerRoundsAdapter(this, 0, playerRounds);
		playerRoundsView.setAdapter(playerRoundAdapter);
	}

	protected Button createDismissButton(String text) {
		Button dismissButton = new Button(this);
		dismissButton.setText(text);

		dismissButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SingleRound.this.finish();
			}
		});

		return dismissButton;
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
