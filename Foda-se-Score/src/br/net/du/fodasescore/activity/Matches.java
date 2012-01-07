package br.net.du.fodasescore.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fodasescore.R;
import br.net.du.fodasescore.model.Match;

public class Matches extends Activity {
	private List<Match> matches = new ArrayList<Match>();
	private ArrayAdapter<Match> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matches);

		// TODO: dummy
		matches.add(new Match());
		matches.add(new Match());
		matches.add(new Match());

		final ListView matchesList = (ListView) findViewById(R.id_matches.matchesList);
		adapter = new ArrayAdapter<Match>(this,
				android.R.layout.simple_list_item_1, matches);
		matchesList.setAdapter(adapter);

		matchesList.setClickable(true);

		matchesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO: dummy
				Toast.makeText(Matches.this, "clicked on item " + id,
						Toast.LENGTH_SHORT).show();
			}
		});

	}
}