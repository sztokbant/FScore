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

public class RoundBetsReader extends RoundReader {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TextView instructions = (TextView) findViewById(R.id_singleround.text_instructions);
		instructions.setText(getString(R.string.enter_bets_for_each_player));
	}

	@Override
	protected void addCustomWindowTitle() {
		windowTitle.append(" - " + getString(R.string.bets));
	}

	@Override
	protected AlertDialog.Builder getInputDialog() throws IllegalStateException {
		if (round.hasAnyWins()) {
			throw new IllegalStateException(
					getString(R.string.cannot_set_bet_after_wins));
		}

		final EditText betInput = new EditText(this);
		betInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

		return new AlertDialog.Builder(this)
				.setTitle(selectedPlayerRound.getPlayer().toString())
				.setMessage(getString(R.string.bets))
				.setView(betInput)
				.setPositiveButton(getString(R.string.ok),
						getDoMakeBetClick(betInput))
				.setNegativeButton(getString(R.string.cancel), null);
	}

	@Override
	protected void validateInput() throws FScoreException {
		round.validateBets();
	}

	private OnClickListener getDoMakeBetClick(final EditText betInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editable value = betInput.getText();

				try {
					long bet = Long.parseLong(value.toString());
					round.setBetRelaxed(selectedPlayerRound.getPlayer(), bet);
					playerRoundAdapter.notifyDataSetChanged();
				} catch (NumberFormatException e) {
					new ActivityUtils().showErrorDialog(RoundBetsReader.this,
							getString(R.string.msg_enter_number_between_0_and)
									+ " " + round.getNumberOfCards() + ".");
				} catch (FScoreException e) {
					new ActivityUtils().showErrorDialog(RoundBetsReader.this,
							getString(e.getMessageId()));
				}
			}
		};
	}
}
