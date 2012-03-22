package br.net.du.fscore.activity;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.exceptions.FScoreException;

public abstract class RoundReader extends SingleRound {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createDismissButton(getString(R.string.cancel));
		createSaveButton();
	}

	@Override
	protected void createPlayerRoundsListAdapter() {
		super.createPlayerRoundsListAdapter();
		createListViewClickListeners();
	}

	protected abstract void validateInput() throws FScoreException;

	protected abstract AlertDialog.Builder getInputDialog()
			throws IllegalStateException;

	private void createSaveButton() {
		Button saveButton = (Button) findViewById(R.id_singleround.okbtn);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					validateInput();
					dataManager.saveMatch(match);
					RoundReader.this.finish();
				} catch (FScoreException e) {
					new ActivityUtils().showErrorDialog(RoundReader.this,
							getString(e.getMessageId()));
				}
			}
		});
	}

	private void createListViewClickListeners() {
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
					new ActivityUtils().showErrorDialog(RoundReader.this,
							e.getMessage());
				}
			}
		});

		registerForContextMenu(playerRoundsView);
	}
}
