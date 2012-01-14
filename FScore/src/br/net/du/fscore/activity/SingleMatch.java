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

	private String lastClickedName = "";
	private Player selectedPlayer;

	private TabHost tabHost;

	private Match match;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roundlist);

		match = (Match) getIntent().getSerializableExtra("selectedMatch");

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		roundList = (ListView) findViewById(R.id_match.roundlist);
		roundList.setClickable(true);
		updateRoundsList();

		playerList = (ListView) findViewById(R.id_match.playerlist);
		playerList.setClickable(true);
		updatePlayersList();

		playerList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				// for context menu title
				lastClickedName = match.getPlayersAsList().get(position)
						.getName();

				// for editing/deleting
				selectedPlayer = (Player) playerAdapter.getItem(position);

				// won't consume the action
				return false;
			}
		});

		registerForContextMenu(playerList);

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
		menu.setHeaderTitle(lastClickedName);

		MenuItem delete = menu.add(0, 0, 0, "Delete");

		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleMatch.this).setTitle("Delete")
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(
										SingleMatch.this,
										"Deleting " + selectedPlayer.getName()
												+ "...", Toast.LENGTH_SHORT)
										.show();

								// DAO code goes here!

								// it's not necessary to reload the full list
								match.getPlayers().remove(selectedPlayer);
								// playerAdapter.notifyDataSetChanged();
								updatePlayersList();
							}
						}).setNegativeButton("No", null).show();

				return true;
			}
		});
	}

	private void updateRoundsList() {
		List<Round> rounds = match.getRounds();
		roundAdapter = new ArrayAdapter<Round>(this,
				android.R.layout.simple_list_item_1, rounds);
		roundList.setAdapter(roundAdapter);
	}

	private void updatePlayersList() {
		List<Player> players = match.getPlayersAsList();
		playerAdapter = new ArrayAdapter<Player>(this,
				android.R.layout.simple_list_item_1, players);
		playerList.setAdapter(playerAdapter);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addPlayer = menu.add(0, 0, 0, "Add Player");

		final Intent chooser = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		addPlayer.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivityForResult(chooser, CONTACT_SELECTED_RESULT_ID);
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			// Add Player
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case (CONTACT_SELECTED_RESULT_ID):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					int nameIndex = c
							.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
					String name = c.getString(nameIndex);
					match.withPlayer(new Player(name));
					updatePlayersList();
				}
			}
		}
	}

	@Override
	public void onTabChanged(String tabName) {
		if (tabName.equals(PLAYERS_TAB_TAG)) {
			Toast.makeText(
					this,
					"selected Player's tab (total: "
							+ match.getPlayers().size() + ")",
					Toast.LENGTH_SHORT).show();
		} else if (tabName.equals(ROUNDS_TAB_TAG)) {
			Toast.makeText(
					this,
					"selected Round's tab (total: " + match.getRounds().size()
							+ ")", Toast.LENGTH_SHORT).show();
		}
	}
}