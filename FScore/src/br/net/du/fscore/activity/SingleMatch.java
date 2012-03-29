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
import br.net.du.fscore.activity.adapters.PlayerScoresAdapter;
import br.net.du.fscore.activity.adapters.RoundsAdapter;
import br.net.du.fscore.activity.util.ActivityUtils;
import br.net.du.fscore.model.Match;
import br.net.du.fscore.model.Player;
import br.net.du.fscore.model.PlayerScore;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.model.exceptions.FScoreException;
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

		matchId = (Long) getIntent().getSerializableExtra("matchId");

		try {
			match = dataManager.retrieveMatch(matchId);
		} catch (FScoreException e) {
			new ActivityUtils().showErrorDialog(SingleMatch.this,
					getString(e.getMessageId()));
		}

		SingleMatch.this.setTitle(getString(R.string.match) + " - "
				+ match.getName());

		createAddPlayerButton();
		createNewRoundButton();

		createRoundsListAdapter();
		createPlayersListAdapter();

		String selectedTabTag = (String) getIntent().getSerializableExtra(
				"selectedTabTag");
		makeTabs(selectedTabTag);
	}

	private void createNewRoundButton() {
		newRoundBtn = new Button(SingleMatch.this);
		newRoundBtn.setGravity(Gravity.CENTER_VERTICAL + Gravity.LEFT);
		newRoundBtn.setTypeface(Typeface.DEFAULT_BOLD);
		newRoundBtn.setCompoundDrawablePadding(8);
		newRoundBtn.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.new_round, 0, 0, 0);

		newRoundBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!rounds.isEmpty()
						&& !rounds.get(rounds.size() - 1).isComplete()) {
					selectedRound = rounds.get(rounds.size() - 1);

					Toast.makeText(SingleMatch.this,
							getString(R.string.last_round_incomplete),
							Toast.LENGTH_SHORT).show();

					openSelectedRound();
				} else {
					createNewRound();
				}
			}
		});
	}

	private void createAddPlayerButton() {
		addPlayerBtn = new Button(SingleMatch.this);
		addPlayerBtn.setGravity(Gravity.CENTER_VERTICAL + Gravity.LEFT);
		addPlayerBtn.setTypeface(Typeface.DEFAULT_BOLD);
		addPlayerBtn.setCompoundDrawablePadding(8);
		addPlayerBtn.setText(getString(R.string.add_player));
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

		try {
			match = dataManager.retrieveMatch(matchId);
			refreshPlayersTab();
			refreshRoundsTab();
		} catch (FScoreException e) {
			new ActivityUtils().showErrorDialog(SingleMatch.this,
					getString(e.getMessageId()));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataManager.closeDb();
	}

	private void createNewRound() {
		try {
			selectedRound = match.newRound();
			dataManager.saveMatch(match);
			unregisterForContextMenu(playerScoresView);
			openSelectedRound();
		} catch (FScoreException e) {
			new ActivityUtils().showErrorDialog(SingleMatch.this,
					getString(e.getMessageId()));
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (view == playerScoresView) {
			menu.setHeaderTitle(selectedPlayer.toString());
			MenuItem delete = menu.add(0, 0, 0,
					getString(R.string.delete_player));
			delete.setOnMenuItemClickListener(playerDeleteDialog());
		} else {
			menu.setHeaderTitle(selectedRound.toString());
			MenuItem bet = menu.add(0, 0, 0, getString(R.string.bets));
			bet.setOnMenuItemClickListener(openSelectedRoundClickListener());
			MenuItem wins = menu.add(0, 1, 0, getString(R.string.wins));
			wins.setOnMenuItemClickListener(openSelectedRoundClickListener());
		}
	}

	private OnMenuItemClickListener openSelectedRoundClickListener() {
		return new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == 0) {
					openSelectedRound(RoundBetsReader.class);
				} else if (item.getItemId() == 1) {
					openSelectedRound(RoundWinsReader.class);
				}

				return true;
			}
		};
	}

	@Override
	public void onTabChanged(String tabName) {
		if (tabName.equals(PLAYERS_TAB_TAG)) {
			refreshPlayersTab();
		} else if (tabName.equals(ROUNDS_TAB_TAG)) {
			refreshRoundsTab();
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
				.setTitle(R.string.add_player_to_match)
				.setMessage(getString(R.string.player_name))
				.setView(input)
				.setPositiveButton(getString(R.string.ok),
						getDoAddPlayerClick(input))
				.setNegativeButton(getString(R.string.cancel), null);
	}

	private OnClickListener getDoAddPlayerClick(final EditText nameInput) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameInput.getText().toString();

				try {
					Player player = new Player(name);
					match.with(player);
					dataManager.saveMatch(match);
					tabHost.setCurrentTabByTag(PLAYERS_TAB_TAG);
					refreshPlayersTab();
				} catch (FScoreException e) {
					new ActivityUtils().showErrorDialog(SingleMatch.this,
							getString(e.getMessageId()));
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
						.setTitle(
								getString(R.string.delete) + " "
										+ selectedPlayer)
						.setMessage(getString(R.string.are_you_sure))
						.setPositiveButton(getString(R.string.yes),
								getDoDeletePlayerClick())
						.setNegativeButton(getString(R.string.no), null).show();

				return true;
			}

			private OnClickListener getDoDeletePlayerClick() {
				return new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean playerDeleted;
						try {
							playerDeleted = match.deletePlayer(selectedPlayer);

							if (playerDeleted) {
								Toast.makeText(
										SingleMatch.this,
										"'"
												+ selectedPlayer
												+ "' "
												+ getString(R.string.player_deleted),
										Toast.LENGTH_SHORT).show();

								dataManager.saveMatch(match);
								refreshPlayersTab();
							}
						} catch (FScoreException e) {
							new ActivityUtils().showErrorDialog(
									SingleMatch.this,
									getString(e.getMessageId()));
						}
					}
				};
			}
		};
	}

	private void makeTabs(String selectedTabTag) {
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		tabHost.addTab(tabHost
				.newTabSpec(PLAYERS_TAB_TAG)
				.setIndicator(getString(R.string.players),
						getResources().getDrawable(R.drawable.players))
				.setContent(SingleMatch.this));

		tabHost.addTab(tabHost
				.newTabSpec(ROUNDS_TAB_TAG)
				.setIndicator(getString(R.string.rounds),
						getResources().getDrawable(R.drawable.rounds))
				.setContent(SingleMatch.this));

		tabHost.setCurrentTabByTag(selectedTabTag);
	}

	private void createPlayersListAdapter() {
		playerScoresView = new ListView(this);
		playerScoresAdapter = new PlayerScoresAdapter(this, 0, playerScores);
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
		roundAdapter = new RoundsAdapter(this, 0, rounds);
		roundView.setAdapter(roundAdapter);

		roundView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectedRound = (Round) roundAdapter.getItem(position);
				openSelectedRound();
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

	protected void openSelectedRound() {
		Class<?> destinationActivity;

		if (selectedRound.isComplete()) {
			destinationActivity = SingleRound.class;
		} else if (!selectedRound.hasAllBets()) {
			destinationActivity = RoundBetsReader.class;
		} else {
			destinationActivity = RoundWinsReader.class;
		}

		openSelectedRound(destinationActivity);
	}

	private void openSelectedRound(Class<?> destinationActivity) {
		Intent singleRound = new Intent(SingleMatch.this, destinationActivity);
		singleRound.putExtra("selectedRoundId", selectedRound.getId());
		singleRound.putExtra("matchId", match.getId());
		startActivity(singleRound);
	}

	private void refreshPlayersTab() {
		if (!match.getRounds().isEmpty()) {
			addPlayerBtn.setEnabled(false);
			addPlayerBtn
					.setText(getString(R.string.cannot_add_players_anymore));
		}

		playerScores.clear();
		playerScores.addAll(match.getPlayerScores());
		Collections.sort(playerScores);
		playerScoresAdapter.notifyDataSetChanged();
	}

	private void refreshRoundsTab() {
		if (match.getPlayers().size() < 2) {
			newRoundBtn.setEnabled(false);
			newRoundBtn.setText(getString(R.string.add_some_players_first));
		} else {
			newRoundBtn.setEnabled(true);
			if (match.getRounds().isEmpty()) {
				newRoundBtn.setText(getString(R.string.start_match));
			} else {
				newRoundBtn.setText(getString(R.string.begin_next_round));
			}
		}

		rounds.clear();
		rounds.addAll(match.getRounds());
		roundAdapter.notifyDataSetChanged();
	}
}
