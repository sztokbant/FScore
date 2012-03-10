package br.net.du.fscore.activity.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;

public class MatchesAdapter extends ArrayAdapter<Match> {
	private Context context;

	public MatchesAdapter(Context context, int textViewResourceId,
			List<Match> matches) {
		super(context, textViewResourceId, matches);
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		TextView item = new TextView(context);
		Match match = getItem(position);
		item.setText(match.getName() + "\n" + match.getFormattedWhen());
		item.setTextSize(new ActivityUtils().getAdapterFontSize());
		item.setPadding(new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding(),
				new ActivityUtils().getAdapterHorizontalPadding(),
				new ActivityUtils().getAdapterVerticalPadding());
		return item;
	}
}
