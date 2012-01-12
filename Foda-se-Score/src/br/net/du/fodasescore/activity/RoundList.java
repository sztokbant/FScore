package br.net.du.fodasescore.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;
import br.net.du.fodasescore.R;
import br.net.du.fodasescore.model.Match;
import br.net.du.fodasescore.model.Player;
import br.net.du.fodasescore.model.Round;

// Tabs based on tutorial on http://joshclemm.com/blog/?p=59
public class RoundList extends TabActivity implements OnTabChangeListener {

	// used by "Add Player"
	public static final int CONTACT_SELECTED_RESULT_ID = 1;

	public static final String ROUNDS_TAB_TAG = "Rounds";
	public static final String PLAYERS_TAB_TAG = "Players";

	private ListView roundList;
	private ListView playerList;

	private TabHost tabHost;

	private Match match;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roundlist);

		match = (Match) getIntent().getSerializableExtra("selectedMatch");

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		List<Round> rounds = match.getRounds();
		roundList = (ListView) findViewById(R.id_match.roundlist);
		final ArrayAdapter<Round> roundAdapter = new ArrayAdapter<Round>(this,
				android.R.layout.simple_list_item_1, rounds);
		roundList.setClickable(true);

		List<Player> players = new ArrayList<Player>();
		for (Player p : match.getPlayers()) {
			players.add(p);
		}
		playerList = (ListView) findViewById(R.id_match.playerlist);
		final ArrayAdapter<Player> playerAdapter = new ArrayAdapter<Player>(
				this, android.R.layout.simple_list_item_1, players);
		roundList.setClickable(true);

		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec(ROUNDS_TAB_TAG)
				.setIndicator(ROUNDS_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						roundList.setAdapter(roundAdapter);
						return roundList;
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(PLAYERS_TAB_TAG)
				.setIndicator(PLAYERS_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						playerList.setAdapter(playerAdapter);
						return playerList;
					}
				}));
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
			Toast.makeText(RoundList.this, "'Add Player'", Toast.LENGTH_SHORT)
					.show();
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
					Toast.makeText(this, "Added player: " + name,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void onTabChanged(String tabName) {
		if (tabName.equals(PLAYERS_TAB_TAG)) {
			Toast.makeText(this, "selected Player's tab", Toast.LENGTH_SHORT)
					.show();
		} else if (tabName.equals(ROUNDS_TAB_TAG)) {
			Toast.makeText(this, "selected Round's tab", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
