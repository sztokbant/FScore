package br.net.du.fscore.activity.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
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
		TextView item = new TextView(context);
		item.setText(getItem(position).toString());
		return item;
	}
}
