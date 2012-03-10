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

		TextView betWins = new TextView(context);
		betWins.setText("(" + playerRound.getWinsRepresentation() + "/"
				+ playerRound.getBetRepresentation() + ")");
		betWins.setGravity(Gravity.RIGHT + Gravity.CENTER_VERTICAL);
		betWins.setTextSize(new ActivityUtils().getAdapterFontSize());
		betWins.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());

		TextView score = new TextView(context);
		score.setText(String.valueOf(playerRound.getScore()));
		score.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		score.setGravity(Gravity.RIGHT + Gravity.CENTER_VERTICAL);
		score.setTextSize(new ActivityUtils().getAdapterFontSize());
		score.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());

		layout.addView(name);
		layout.addView(betWins);
		layout.addView(score);

		return layout;
	}
}
