package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.persist.DataManager;

public class MatchList extends Activity {
	private List<Match> matches = new ArrayList<Match>();
	private ArrayAdapter<Match> adapter;
	private Match selectedMatch;
	private DataManager dataManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matchlist);

		dataManager = new DataManager(this);

		final ListView matchesList = (ListView) findViewById(R.id_matchlist.matchlist);
		adapter = new ArrayAdapter<Match>(this,
				android.R.layout.simple_list_item_1, matches);
		matchesList.setAdapter(adapter);

		matchesList.setOnItemClickListener(new OnItemClickListener() {
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

		matchesList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for context menu title and deleting
				selectedMatch = matches.get(position);

				// won't consume the action
				return false;
			}
		});

		registerForContextMenu(matchesList);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuItem newMatch =
		menu.add(0, 0, 0, "New Match");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataManager.closeDb();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dataManager.openDb();
		refreshMatchList();
	}

	private void refreshMatchList() {
		matches.clear();
		matches.addAll(dataManager.retrieveAllMatches());
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			// New Match
			Match match = new Match("Match "
					+ Integer.toString(new Random().nextInt()));
			matches.add(match);
			dataManager.saveMatch(match);
			adapter.notifyDataSetChanged();
		}
		return false;
	}

	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(selectedMatch.getName());

		MenuItem delete = menu.add(0, 0, 0, "Delete");

		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(MatchList.this).setTitle("Delete")
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(
										MatchList.this,
										"Deleting " + selectedMatch.getName()
												+ "...", Toast.LENGTH_SHORT)
										.show();

								dataManager.deleteMatch(selectedMatch);

								// it's not necessary to reload the full list
								matches.remove(selectedMatch);
								adapter.notifyDataSetChanged();
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		});
	}
}