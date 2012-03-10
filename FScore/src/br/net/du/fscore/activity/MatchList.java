package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.adapters.MatchesAdapter;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.exceptions.FScoreException;
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

		Button newMatchBtn = (Button) findViewById(R.id_matchlist.new_match_button);
		newMatchBtn.setOnClickListener(newMatchOnClickListener());

		dataManager = new DataManager(this);
		createMatchListAdapter();
	}

	private android.view.View.OnClickListener newMatchOnClickListener() {
		return new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final EditText input = new EditText(MatchList.this);

				new AlertDialog.Builder(MatchList.this)
						.setTitle(getString(R.string.new_f_match))
						.setMessage(getString(R.string.match_desc))
						.setView(input)
						.setPositiveButton(getString(R.string.ok),
								getDoNewMatchClick(input))
						.setNegativeButton(getString(R.string.cancel), null)
						.show();
			}
		};
	}

	private OnClickListener getDoNewMatchClick(final EditText nameInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameInput.getText().toString().trim();

				if (name.equals("")) {
					new ActivityUtils().showErrorDialog(MatchList.this,
							getString(R.string.msg_enter_new_match_desc));
				} else {
					Match match = new Match(name);
					matches.add(0, match);
					try {
						dataManager.saveMatch(match);
						adapter.notifyDataSetChanged();
						openMatch(match);
					} catch (FScoreException e) {
						new ActivityUtils().showErrorDialog(MatchList.this,
								getString(e.getMessageId()));
					}
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		dataManager.openDb();
		try {
			refreshMatchList();
		} catch (FScoreException e) {
			new ActivityUtils().showErrorDialog(MatchList.this,
					getString(e.getMessageId()));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataManager.closeDb();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(selectedMatch.getName());

		MenuItem editDescription = menu.add(0, 0, 0,
				getString(R.string.edit_desc));
		editDescription.setOnMenuItemClickListener(editDescriptionDialog());

		MenuItem delete = menu.add(0, 1, 0, getString(R.string.delete_match));
		delete.setOnMenuItemClickListener(matchDeleteDialog());
	}

	private OnMenuItemClickListener matchDeleteDialog() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new AlertDialog.Builder(MatchList.this)
						.setTitle(
								getString(R.string.deleting) + " '"
										+ selectedMatch.getName() + "'")
						.setMessage(getString(R.string.are_you_sure))
						.setPositiveButton(getString(R.string.yes),
								getDoMatchDeleteClick())
						.setNegativeButton(getString(R.string.no), null).show();

				return true;
			}

			private OnClickListener getDoMatchDeleteClick() {
				return new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dataManager.deleteMatch(selectedMatch);

						Toast.makeText(
								MatchList.this,
								"'" + selectedMatch.getName() + "' "
										+ getString(R.string.match_deleted),
								Toast.LENGTH_SHORT).show();

						// it's not necessary to reload the full list
						matches.remove(selectedMatch);
						adapter.notifyDataSetChanged();
					}
				};
			}
		};
	}

	private OnMenuItemClickListener editDescriptionDialog() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final EditText input = new EditText(MatchList.this);

				new AlertDialog.Builder(MatchList.this)
						.setTitle(getString(R.string.f_match))
						.setMessage(getString(R.string.enter_new_desc))
						.setView(input)
						.setPositiveButton(getString(R.string.ok),
								getDoUpdateDescription(input))
						.setNegativeButton(getString(R.string.cancel), null)
						.show();

				return true;
			}

			private OnClickListener getDoUpdateDescription(
					final EditText nameInput) {
				return new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = nameInput.getText().toString().trim();

						if (name.equals("")) {
							new ActivityUtils().showErrorDialog(MatchList.this,
									getString(R.string.msg_enter_match_desc));
						} else {
							selectedMatch.setName(name);
							try {
								dataManager.saveMatch(selectedMatch);
								adapter.notifyDataSetChanged();
							} catch (FScoreException e) {
								new ActivityUtils().showErrorDialog(
										MatchList.this,
										getString(e.getMessageId()));
							}
						}
					}
				};
			}
		};
	}

	private void createMatchListAdapter() {
		final ListView matchesView = (ListView) findViewById(R.id_matchlist.matchlist);
		adapter = new MatchesAdapter(this, 0, matches);
		matchesView.setAdapter(adapter);

		matchesView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				openMatch((Match) adapter.getItem(position));
			}
		});

		matchesView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for context menu title and deleting
				selectedMatch = matches.get(position);

				// won't consume the action
				return false;
			}
		});

		registerForContextMenu(matchesView);
	}

	protected void openMatch(Match match) {
		selectedMatch = match;
		Intent singleMatch = new Intent(MatchList.this, SingleMatch.class);
		singleMatch.putExtra("selectedMatchId", selectedMatch.getId());
		startActivity(singleMatch);
	}

	private void refreshMatchList() throws FScoreException {
		matches.clear();
		matches.addAll(dataManager.retrieveAllMatches());
		adapter.notifyDataSetChanged();
	}
}