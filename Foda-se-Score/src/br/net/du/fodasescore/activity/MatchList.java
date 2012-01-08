package br.net.du.fodasescore.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fodasescore.R;
import br.net.du.fodasescore.model.Match;
import br.net.du.fodasescore.model.Round;

public class MatchList extends Activity {
	private List<Match> matches = new ArrayList<Match>();
	private ArrayAdapter<Match> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matchlist);

		// TODO: dummy
		Match m1 = new Match();
		m1.addRound(new Round());
		Match m2 = new Match();
		m2.addRound(new Round());
		m2.addRound(new Round());
		Match m3 = new Match();
		m3.addRound(new Round());
		m3.addRound(new Round());
		m3.addRound(new Round());
		matches.add(m1);
		matches.add(m2);
		matches.add(m3);

		final ListView matchesList = (ListView) findViewById(R.id_matchlist.matchlist);
		adapter = new ArrayAdapter<Match>(this,
				android.R.layout.simple_list_item_1, matches);
		matchesList.setAdapter(adapter);

		matchesList.setClickable(true);

		matchesList.setOnItemClickListener(new OnItemClickListener() {
			private Match selectedMatch;

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO: dummy
				selectedMatch = (Match) adapter.getItem(position);
				Toast.makeText(MatchList.this, "Opening Match " + selectedMatch,
						Toast.LENGTH_SHORT).show();

				Intent match = new Intent(MatchList.this, RoundList.class);
				match.putExtra("selectedMatch", selectedMatch);
				startActivity(match);
			}
		});

	}
}