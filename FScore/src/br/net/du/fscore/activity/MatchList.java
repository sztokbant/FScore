package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.persist.MatchDAO;

public class MatchList extends Activity {
	private List<Match> matches = new ArrayList<Match>();
	private ArrayAdapter<Match> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matchlist);

		final ListView matchesList = (ListView) findViewById(R.id_matchlist.matchlist);
		adapter = new ArrayAdapter<Match>(this,
				android.R.layout.simple_list_item_1, matches);
		matchesList.setAdapter(adapter);

		matchesList.setOnItemClickListener(new OnItemClickListener() {
			private Match selectedMatch;

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectedMatch = (Match) adapter.getItem(position);
				Toast.makeText(MatchList.this,
						"Opening Match " + selectedMatch, Toast.LENGTH_SHORT)
						.show();

				Intent match = new Intent(MatchList.this, SingleMatch.class);
				match.putExtra("selectedMatch", selectedMatch);
				startActivity(match);
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem newMatch = menu.add(0, 0, 0, "New Match");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshMatchList();
	}

	private void refreshMatchList() {
		MatchDAO dao = new MatchDAO(this);
		matches.clear();
		matches.addAll(dao.getList());
		dao.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			// New Match
			Match match = new Match("Match "
					+ Integer.toString(new Random().nextInt()));
			matches.add(match);

			MatchDAO dao = new MatchDAO(this);
			dao.save(match);
			dao.close();

			adapter.notifyDataSetChanged();
		}
		return false;
	}
}