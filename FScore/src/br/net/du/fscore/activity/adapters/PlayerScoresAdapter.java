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
import br.net.du.fscore.model.PlayerScore;

public class PlayerScoresAdapter extends ArrayAdapter<PlayerScore> {
	private Context context;

	public PlayerScoresAdapter(Context context, int textViewResourceId,
			List<PlayerScore> playerScores) {
		super(context, textViewResourceId, playerScores);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		PlayerScore playerScore = getItem(position);

		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);

		TextView name = new TextView(context);
		name.setText(playerScore.getPlayer().getName());
		name.setGravity(Gravity.LEFT + Gravity.CENTER_VERTICAL);
		name.setTextSize(new ActivityUtils().getAdapterFontSize());
		name.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());

		TextView score = new TextView(context);
		score.setText(String.valueOf(playerScore.getScore()));
		score.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		score.setGravity(Gravity.RIGHT + Gravity.CENTER_VERTICAL);
		score.setTextSize(new ActivityUtils().getAdapterFontSize());
		score.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());

		layout.addView(name);
		layout.addView(score);

		return layout;
	}
}
