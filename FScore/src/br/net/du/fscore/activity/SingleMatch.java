package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;

// Tabs based on tutorial on http://joshclemm.com/blog/?p=59
public class SingleMatch extends TabActivity implements OnTabChangeListener {

	// used by "Add Player"
	public static final int CONTACT_SELECTED_RESULT_ID = 1;

	public static final String ROUNDS_TAB_TAG = "Rounds";
	public static final String PLAYERS_TAB_TAG = "Players";

	private Match match;
	private long matchId;

	private ListView roundView;
	private ArrayAdapter<Round> roundAdapter;
	private List<Round> rounds = new ArrayList<Round>();
	private Round selectedRound;

	private ListView playerView;
	private ArrayAdapter<Player> playerAdapter;
	private List<Player> players = new ArrayList<Player>();
	private Player selectedPlayer;

	private TabHost tabHost;

	private DataManager dataManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlematch);

		dataManager = new DataManager(this);

		matchId = (Long) getIntent().getSerializableExtra("selectedMatchId");
		match = dataManager.retrieveMatch(matchId);

		createRoundsListAdapter();
		createPlayersListAdapter();
		makeTabs();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dataManager.openDb();

		match = dataManager.retrieveMatch(matchId);
		refreshPlayersList();
		refreshRoundsList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataManager.closeDb();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuItem addPlayer =
		menu.add(0, 0, 0, "Add Player");
		// MenuItem addRound =
		menu.add(0, 1, 0, "Add Round");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			// Add Player
			if (!match.getRounds().isEmpty()) {
				Toast.makeText(SingleMatch.this,
						"Cannot add more players after match has started.",
						Toast.LENGTH_SHORT).show();
			} else {
				Intent chooser = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(chooser, CONTACT_SELECTED_RESULT_ID);
			}
		} else if (item.getItemId() == 1) {
			// Add Round
			// TODO this Round is dummy
			Round round = new Round(7);
			match.addRound(round);
			dataManager.saveMatch(match);

			if (tabHost.getCurrentTabTag() == ROUNDS_TAB_TAG) {
				refreshRoundsList();
			}

			unregisterForContextMenu(playerView);
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (view == playerView) {
			menu.setHeaderTitle(selectedPlayer.toString());
			MenuItem delete = menu.add(0, 0, 0, "Delete");
			delete.setOnMenuItemClickListener(playerDeleteClickListener());
		} else if (view == roundView) {
			menu.setHeaderTitle(selectedRound.toString());
			MenuItem delete = menu.add(0, 0, 0, "Delete");
			delete.setOnMenuItemClickListener(roundDeleteClickListener());
		}
	}

	@Override
	public void onTabChanged(String tabName) {
		if (tabName.equals(PLAYERS_TAB_TAG)) {
			refreshPlayersList();
		} else if (tabName.equals(ROUNDS_TAB_TAG)) {
			refreshRoundsList();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dataManager.openDb();

		switch (requestCode) {
		case (CONTACT_SELECTED_RESULT_ID):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();

				Cursor cursor = managedQuery(contactData, null, null, null,
						null);

				if (cursor.moveToFirst()) {
					int nameIndex = cursor
							.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
					String name = cursor.getString(nameIndex);

					// sanity check
					if (name == null) {
						name = "(Unknown)";
					}

					Player player = new Player(name);
					match.withPlayer(player);
					dataManager.saveMatch(match);
					if (tabHost.getCurrentTabTag() == PLAYERS_TAB_TAG) {
						refreshPlayersList();
					}
				} else {
					Toast.makeText(SingleMatch.this,
							"Contact not supported =(", Toast.LENGTH_SHORT)
							.show();
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}

	private OnMenuItemClickListener playerDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleMatch.this)
						.setTitle("Delete " + selectedPlayer)
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									boolean playerDeleted = match
											.deletePlayer(selectedPlayer);
									if (playerDeleted) {
										Toast.makeText(
												SingleMatch.this,
												"Deleting " + selectedPlayer
														+ "...",
												Toast.LENGTH_SHORT).show();

										dataManager.saveMatch(match);
										refreshPlayersList();
									}
								} catch (IllegalStateException e) {
									Toast.makeText(
											SingleMatch.this,
											"Cannot delete players belonging to rounds",
											Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		};
	}

	private OnMenuItemClickListener roundDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleMatch.this)
						.setTitle("Delete " + selectedRound)
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(SingleMatch.this,
										"Deleting " + selectedRound + "...",
										Toast.LENGTH_SHORT).show();

								match.getRounds().remove(selectedRound);
								dataManager.saveMatch(match);
								refreshRoundsList();

								if (match.getRounds().isEmpty()) {
									registerForContextMenu(playerView);
								}
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		};
	}

	private void makeTabs() {
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		tabHost.addTab(tabHost.newTabSpec(ROUNDS_TAB_TAG)
				.setIndicator(ROUNDS_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return roundView;
					}
				}));

		tabHost.addTab(tabHost.newTabSpec(PLAYERS_TAB_TAG)
				.setIndicator(PLAYERS_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return playerView;
					}
				}));
	}

	private void createPlayersListAdapter() {
		playerView = new ListView(this);
		playerAdapter = new ArrayAdapter<Player>(this,
				android.R.layout.simple_list_item_1, players);
		playerView.setAdapter(playerAdapter);

		playerView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for editing/deleting
				selectedPlayer = (Player) playerAdapter.getItem(position);
				// won't consume the action
				return false;
			}
		});

		if (match.getRounds().isEmpty()) {
			registerForContextMenu(playerView);
		}
	}

	private void createRoundsListAdapter() {
		roundView = new ListView(this);
		roundAdapter = new ArrayAdapter<Round>(this,
				android.R.layout.simple_list_item_1, rounds);
		roundView.setAdapter(roundAdapter);

		roundView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectedRound = (Round) roundAdapter.getItem(position);
				Toast.makeText(SingleMatch.this,
						"Opening Round " + selectedRound, Toast.LENGTH_SHORT)
						.show();
				Intent singleRound = new Intent(SingleMatch.this,
						SingleRound.class);
				singleRound.putExtra("selectedRoundId", selectedRound.getId());
				singleRound.putExtra("matchId", match.getId());
				startActivity(singleRound);
			}
		});

		roundView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for editing/deleting
				selectedRound = (Round) roundAdapter.getItem(position);
				// won't consume the action
				return false;
			}
		});

		registerForContextMenu(roundView);
	}

	private void refreshPlayersList() {
		players.clear();
		players.addAll(match.getPlayers());
		Collections.sort(players);
		playerAdapter.notifyDataSetChanged();
	}

	private void refreshRoundsList() {
		rounds.clear();
		rounds.addAll(match.getRounds());
		roundAdapter.notifyDataSetChanged();
	}
}
