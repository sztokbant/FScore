package br.net.du.fscore.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.exceptions.FScoreException;

public class RoundWinsReader extends SingleRound {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TextView instructions = (TextView) findViewById(R.id_singleround.text_instructions);
		instructions.setText(getString(R.string.enter_wins_for_each_player));
	}

	@Override
	public void onResume() {
		super.onResume();
		windowTitle.append(" - " + getString(R.string.wins));
		this.setTitle(windowTitle);
	}

	@Override
	protected AlertDialog.Builder getInputDialog() throws IllegalStateException {
		if (!round.hasAllBets()) {
			throw new IllegalStateException(
					getString(R.string.cannot_set_wins_before_all_bets));
		}

		final EditText winsInput = new EditText(this);
		winsInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		return new AlertDialog.Builder(this)
				.setTitle(selectedPlayerRound.getPlayer().toString())
				.setMessage(getString(R.string.wins))
				.setView(winsInput)
				.setPositiveButton(getString(R.string.ok),
						getDoMakeWinsClick(winsInput))
				.setNegativeButton(getString(R.string.cancel), null);
	}

	@Override
	protected void validateInput() throws FScoreException {
		round.validateWins();
	}

	private OnClickListener getDoMakeWinsClick(final EditText winsInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editable value = winsInput.getText();

				try {
					long wins = Long.parseLong(value.toString());
					round.setWinsRelaxed(selectedPlayerRound.getPlayer(), wins);
					playerRoundAdapter.notifyDataSetChanged();
				} catch (NumberFormatException e) {
					new ActivityUtils().showErrorDialog(RoundWinsReader.this,
							getString(R.string.msg_enter_number_between_0_and)
									+ " " + round.getNumberOfCards() + ".");
				} catch (FScoreException e) {
					new ActivityUtils().showErrorDialog(RoundWinsReader.this,
							getString(e.getMessageId()));
				}
			}
		};
	}
}
