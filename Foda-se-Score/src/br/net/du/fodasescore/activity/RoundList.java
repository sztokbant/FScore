package br.net.du.fodasescore.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.net.du.fodasescore.R;
import br.net.du.fodasescore.model.Match;
import br.net.du.fodasescore.model.Round;

public class RoundList extends Activity {
	private Match match;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roundlist);

		match = (Match) getIntent().getSerializableExtra("selectedMatch");

		List<Round> rounds = match.getRounds();
		final ListView roundList = (ListView) findViewById(R.id_roundlist.roundlist);
		ArrayAdapter<Round> adapter = new ArrayAdapter<Round>(this,
				android.R.layout.simple_list_item_1, rounds);
		roundList.setAdapter(adapter);

		roundList.setClickable(true);
	}
}
