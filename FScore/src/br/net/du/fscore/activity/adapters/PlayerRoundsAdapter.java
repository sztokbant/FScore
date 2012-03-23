package br.net.du.fscore.activity.adapters;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.RoundBetsReader;
import br.net.du.fscore.activity.RoundWinsReader;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.PlayerRound;

public class PlayerRoundsAdapter extends ArrayAdapter<PlayerRound> {
	private Context context;

	public PlayerRoundsAdapter(Context context, int textViewResourceId,
			List<PlayerRound> playerRounds) {
		super(context, textViewResourceId, playerRounds);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		PlayerRound playerRound = getItem(position);

		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setHorizontalGravity(Gravity.RIGHT);

		TextView name = new TextView(context);
		name.setText(playerRound.getPlayer().getName());
		name.setGravity(Gravity.LEFT + Gravity.CENTER_VERTICAL);
		name.setTextSize(new ActivityUtils().getAdapterFontSize());
		name.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());

		layout.addView(name);

		TextView value = new TextView(context);
		value.setGravity(Gravity.RIGHT + Gravity.CENTER_VERTICAL);
		value.setTextSize(new ActivityUtils().getAdapterFontSize());
		value.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		value.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());

		if (context instanceof RoundBetsReader) {
			value.setText(playerRound.getBetRepresentation());
		} else if (context instanceof RoundWinsReader) {
			value.setText(playerRound.getWinsRepresentation());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("(" + context.getString(R.string.bet) + ": "
					+ playerRound.getBetRepresentation());
			sb.append(" | " + context.getString(R.string.wins) + ": ");
			sb.append(playerRound.getWinsRepresentation());
			sb.append(") ");
			sb.append(playerRound.getScore());

			value.setText(sb.toString());
		}

		layout.addView(value);

		return layout;
	}
}
