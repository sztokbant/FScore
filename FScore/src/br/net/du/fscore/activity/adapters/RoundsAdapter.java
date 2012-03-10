package br.net.du.fscore.activity.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
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
		item.setText(getItem(position).toString());
		return item;
	}
}
