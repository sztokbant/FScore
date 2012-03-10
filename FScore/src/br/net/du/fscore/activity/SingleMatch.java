package br.net.du.fscore.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerScore;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;

// Tabs based on tutorial at http://joshclemm.com/blog/?p=59
public class SingleMatch extends TabActivity implements OnTabChangeListener,
		TabContentFactory {

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

	private ListView playerScoresView;
	private ArrayAdapter<PlayerScore> playerScoresAdapter;
	private List<PlayerScore> playerScores = new ArrayList<PlayerScore>();
	private Player selectedPlayer;

	private TabHost tabHost;

	private DataManager dataManager;

	private Button addPlayerBtn;
	private Button newRoundBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlematch);

		dataManager = new DataManager(this);

		matchId = (Long) getIntent().getSerializableExtra("selectedMatchId");
		match = dataManager.retrieveMatch(matchId);

		SingleMatch.this.setTitle(match.toString());

		createAddPlayerButton();
		createNewRoundButton();

		createRoundsListAdapter();
		createPlayersListAdapter();
		makeTabs();
	}

	private void createNewRoundButton() {
		newRoundBtn = new Button(SingleMatch.this);
		newRoundBtn.setGravity(Gravity.CENTER_VERTICAL + Gravity.LEFT);
		newRoundBtn.setTypeface(Typeface.DEFAULT_BOLD);
		newRoundBtn.setCompoundDrawablePadding(8);
		newRoundBtn.setText("New Round");
		newRoundBtn.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.new_round, 0, 0, 0);

		newRoundBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				createNewRound();
			}
		});
	}

	private void createAddPlayerButton() {
		addPlayerBtn = new Button(SingleMatch.this);
		addPlayerBtn.setGravity(Gravity.CENTER_VERTICAL + Gravity.LEFT);
		addPlayerBtn.setTypeface(Typeface.DEFAULT_BOLD);
		addPlayerBtn.setCompoundDrawablePadding(8);
		addPlayerBtn.setText("Add Player");
		addPlayerBtn.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.add_players, 0, 0, 0);

		addPlayerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getAddPlayerDialog().show();
			}
		});
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

	private void createNewRound() {
		try {
			Round round = match.newRound();
			dataManager.saveMatch(match);
			openRound(round);
			unregisterForContextMenu(playerScoresView);
		} catch (IllegalStateException e) {
			new ActivityUtils().showErrorDialog(SingleMatch.this,
					e.getMessage());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (view == playerScoresView) {
			menu.setHeaderTitle(selectedPlayer.toString());
			MenuItem delete = menu.add(0, 0, 0, "Delete");
			delete.setOnMenuItemClickListener(playerDeleteDialog());
		}
	}

	@Override
	public void onTabChanged(String tabName) {
		if (tabName.equals(PLAYERS_TAB_TAG)) {
			if (!match.getRounds().isEmpty()) {
				addPlayerBtn.setEnabled(false);
			}

			refreshPlayersList();
		} else if (tabName.equals(ROUNDS_TAB_TAG)) {
			refreshRoundsList();
		}
	}

	@Override
	public View createTabContent(String tag) {
		LinearLayout layout = new LinearLayout(SingleMatch.this);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);

		if (tag == PLAYERS_TAB_TAG) {
			layout.addView(addPlayerBtn, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			layout.addView(playerScoresView);
		} else if (tag == ROUNDS_TAB_TAG) {
			layout.addView(newRoundBtn, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			layout.addView(roundView);
		}

		return layout;
	}

	private AlertDialog.Builder getAddPlayerDialog() {
		final EditText input = new EditText(SingleMatch.this);

		return new AlertDialog.Builder(SingleMatch.this)
				.setTitle("Add player to match").setMessage("Player name")
				.setView(input)
				.setPositiveButton("Ok", getDoAddPlayerClick(input))
				.setNegativeButton("Cancel", null);
	}

	private OnClickListener getDoAddPlayerClick(final EditText winsInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editable value = winsInput.getText();

				String name = value.toString();

				try {
					Player player = new Player(name);
					match.with(player);
					dataManager.saveMatch(match);
					tabHost.setCurrentTabByTag(PLAYERS_TAB_TAG);
					refreshPlayersList();
				} catch (Exception e) {
					new ActivityUtils().showErrorDialog(SingleMatch.this,
							e.getMessage());
				}
			}
		};
	}

	private OnMenuItemClickListener playerDeleteDialog() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// delete
				new AlertDialog.Builder(SingleMatch.this)
						.setTitle("Delete " + selectedPlayer)
						.setMessage("Are you sure?")
						.setPositiveButton("Yes", getDoDeletePlayerClick())
						.setNegativeButton("No", null).show();

				return true;
			}

			private OnClickListener getDoDeletePlayerClick() {
				return new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean playerDeleted = match
								.deletePlayer(selectedPlayer);
						if (playerDeleted) {
							Toast.makeText(SingleMatch.this,
									"Deleting " + selectedPlayer + "...",
									Toast.LENGTH_SHORT).show();

							dataManager.saveMatch(match);
							refreshPlayersList();
						}
					}
				};
			}
		};
	}

	private void makeTabs() {
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		tabHost.addTab(tabHost
				.newTabSpec(PLAYERS_TAB_TAG)
				.setIndicator(PLAYERS_TAB_TAG,
						getResources().getDrawable(R.drawable.players))
				.setContent(SingleMatch.this));

		tabHost.addTab(tabHost
				.newTabSpec(ROUNDS_TAB_TAG)
				.setIndicator(ROUNDS_TAB_TAG,
						getResources().getDrawable(R.drawable.rounds))
				.setContent(SingleMatch.this));
	}

	private void createPlayersListAdapter() {
		playerScoresView = new ListView(this);
		playerScoresAdapter = new ArrayAdapter<PlayerScore>(this,
				android.R.layout.simple_list_item_1, playerScores);
		playerScoresView.setAdapter(playerScoresAdapter);

		playerScoresView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						// for editing/deleting
						selectedPlayer = (Player) playerScoresAdapter.getItem(
								position).getPlayer();
						// won't consume the action
						return false;
					}
				});

		if (match.getRounds().isEmpty()) {
			registerForContextMenu(playerScoresView);
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
				openRound((Round) roundAdapter.getItem(position));
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

	protected void openRound(Round round) {
		selectedRound = round;
		Intent singleRound = new Intent(SingleMatch.this, SingleRound.class);
		singleRound.putExtra("selectedRoundId", selectedRound.getId());
		singleRound.putExtra("matchId", match.getId());
		startActivity(singleRound);
	}

	private void refreshPlayersList() {
		playerScores.clear();
		playerScores.addAll(match.getPlayerScores());
		Collections.sort(playerScores);
		playerScoresAdapter.notifyDataSetChanged();
	}

	private void refreshRoundsList() {
		rounds.clear();
		rounds.addAll(match.getRounds());
		roundAdapter.notifyDataSetChanged();
	}
}
