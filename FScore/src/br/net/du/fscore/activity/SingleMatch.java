package br.net.du.fscore.activity;

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
import br.net.du.fscore.persist.DataManagerImpl;

// Tabs based on tutorial on http://joshclemm.com/blog/?p=59
public class SingleMatch extends TabActivity implements OnTabChangeListener {

	// used by "Add Player"
	public static final int CONTACT_SELECTED_RESULT_ID = 1;

	public static final String ROUNDS_TAB_TAG = "Rounds";
	public static final String PLAYERS_TAB_TAG = "Players";

	private ListView roundList;
	private ArrayAdapter<Round> roundAdapter;
	private ListView playerList;
	private ArrayAdapter<Player> playerAdapter;

	private Player selectedPlayer;
	private Round selectedRound;

	private TabHost tabHost;

	private Match match;

	private DataManager dataManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlematch);

		match = (Match) getIntent().getSerializableExtra("selectedMatch");

		dataManager = new DataManagerImpl(this);

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		loadRoundsList();
		loadPlayersList();

		tabHost.addTab(tabHost.newTabSpec(ROUNDS_TAB_TAG)
				.setIndicator(ROUNDS_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return roundList;
					}
				}));

		tabHost.addTab(tabHost.newTabSpec(PLAYERS_TAB_TAG)
				.setIndicator(PLAYERS_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return playerList;
					}
				}));
	}

	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (view == playerList) {
			menu.setHeaderTitle(selectedPlayer.toString());
			MenuItem delete = menu.add(0, 0, 0, "Delete");
			delete.setOnMenuItemClickListener(playerDeleteClickListener());
		} else if (view == roundList) {
			menu.setHeaderTitle(selectedRound.toString());
			MenuItem delete = menu.add(0, 0, 0, "Delete");
			delete.setOnMenuItemClickListener(roundDeleteClickListener());
		}
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
								roundAdapter.notifyDataSetChanged();
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		};
	}

	private OnMenuItemClickListener playerDeleteClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleMatch.this)
						.setTitle("Delete" + selectedPlayer)
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(SingleMatch.this,
										"Deleting " + selectedPlayer + "...",
										Toast.LENGTH_SHORT).show();

								match.getPlayers().remove(selectedPlayer);
								dataManager.saveMatch(match);
								playerAdapter.notifyDataSetChanged();
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		};
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
	}

	private void loadRoundsList() {
		roundList = new ListView(this);
		List<Round> rounds = match.getRounds();
		roundAdapter = new ArrayAdapter<Round>(this,
				android.R.layout.simple_list_item_1, rounds);
		roundList.setAdapter(roundAdapter);

		roundList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for editing/deleting
				selectedRound = (Round) roundAdapter.getItem(position);
				// won't consume the action
				return false;
			}
		});

		registerForContextMenu(roundList);
	}

	private void loadPlayersList() {
		playerList = new ListView(this);
		List<Player> players = match.getPlayers();
		playerAdapter = new ArrayAdapter<Player>(this,
				android.R.layout.simple_list_item_1, players);
		playerList.setAdapter(playerAdapter);

		playerList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for editing/deleting
				selectedPlayer = (Player) playerAdapter.getItem(position);
				// won't consume the action
				return false;
			}
		});

		registerForContextMenu(playerList);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addPlayer = menu.add(0, 0, 0, "Add Player");
		MenuItem addRound = menu.add(0, 1, 0, "Add Round");

		addPlayer.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent chooser = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(chooser, CONTACT_SELECTED_RESULT_ID);
				return false;
			}
		});

		addRound.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO this Round is dummy
				match.addRound(new Round(7));
				if (tabHost.getCurrentTabTag() == ROUNDS_TAB_TAG) {
					roundAdapter.notifyDataSetChanged();
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			// Add Player
		} else if (item.getItemId() == 1) {
			// Add Round
		}
		return false;
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
						playerAdapter.notifyDataSetChanged();
					}
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}

	@Override
	public void onTabChanged(String tabName) {
		if (tabName.equals(PLAYERS_TAB_TAG)) {
			playerAdapter.notifyDataSetChanged();
		} else if (tabName.equals(ROUNDS_TAB_TAG)) {
			roundAdapter.notifyDataSetChanged();
		}
	}
}
