package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

		// TODO not ready
		// createButtons();
	}

	// TODO this method still creates dummy buttons in the wrong position
	private void createButtons() {
		Button ok = new Button(this);
		ok.setGravity(Gravity.CENTER);
		ok.setText("Ok");

		Button cancel = new Button(this);
		cancel.setGravity(Gravity.CENTER);
		cancel.setText("Cancel");

		TableRow tableRow = new TableRow(this);
		tableRow.addView(ok);
		tableRow.addView(cancel);

		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		tableLayout.setGravity(Gravity.BOTTOM);

		tableLayout.addView(tableRow);

		this.addContentView(tableLayout, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		/*
		 * <TableLayout android:id="@+id_singleround/btnlayout"
		 * android:layout_width="fill_parent"
		 * android:layout_height="wrap_content" android:stretchColumns="*" >
		 * 
		 * <TableRow >
		 * 
		 * <Button android:id="@+id_singleround/okbtn"
		 * android:layout_gravity="center" android:text="@+string/ok"
		 * android:textStyle="bold" />
		 * 
		 * <Button android:id="@+id_singleround/dismissbtn"
		 * android:layout_gravity="center" android:textStyle="bold" />
		 * </TableRow> </TableLayout>
		 */
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

	protected void createDismissButton(String text) {
		Button dismissButton = (Button) findViewById(R.id_singleround.dismissbtn);
		dismissButton.setText(text);

		dismissButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SingleRound.this.finish();
			}
		});
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
