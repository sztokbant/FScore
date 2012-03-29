package br.net.du.fscore.activity.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Round;

public class RoundsAdapter extends ArrayAdapter<Round> {
	private Context context;

	public RoundsAdapter(Context context, int textViewResourceId,
			List<Round> rounds) {
		super(context, textViewResourceId, rounds);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		TextView item = new TextView(context);

		Round round = getItem(position);

		String title = context.getString(R.string.round) + " - "
				+ round.getNumberOfCards() + " ";
		title += (round.getNumberOfCards() == 1) ? context
				.getString(R.string.card) : context.getString(R.string.cards);

		if (!round.isComplete()) {
			title += " (";

			if (round.hasAllBets()) {
				title += context.getString(R.string.enter_wins);
			} else {
				title += context.getString(R.string.enter_bets);
			}

			title += ")";
		}

		item.setText(title);
		item.setTextSize(new ActivityUtils().getAdapterFontSize());
		item.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());
		return item;
	}
}
