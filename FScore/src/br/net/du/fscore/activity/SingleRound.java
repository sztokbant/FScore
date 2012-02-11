package br.net.du.fscore.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fscore.R;
import br.net.du.fscore.model.PlayerRound;
import br.net.du.fscore.model.Round;
import br.net.du.fscore.persist.DataManager;

public class SingleRound extends Activity {
	private ArrayAdapter<PlayerRound> adapter;
	private Round round;
	private DataManager dataManager;
	private PlayerRound selectedPlayerRound;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleround);

		round = (Round) getIntent().getSerializableExtra("selectedRound");

		dataManager = new DataManager(this);

		final ListView playerRoundsList = (ListView) findViewById(R.id_singleround.playerroundlist);
		adapter = new ArrayAdapter<PlayerRound>(this,
				android.R.layout.simple_list_item_1, round.getPlayerRounds());
		playerRoundsList.setAdapter(adapter);

		playerRoundsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				selectedPlayerRound = (PlayerRound) adapter.getItem(position);
				Toast.makeText(SingleRound.this,
						"Opening PlayerRound " + selectedPlayerRound,
						Toast.LENGTH_SHORT).show();

//				Intent singlePlayerRound = new Intent(SingleRound.this,
//						SinglePlayerRound.class);
//				singlePlayerRound.putExtra("selectedPlayerRound",
//						selectedPlayerRound);
//				startActivity(singlePlayerRound);
			}
		});

		playerRoundsList
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						// for context menu title and deleting
						selectedPlayerRound = round.getPlayerRounds().get(
								position);

						// won't consume the action
						return false;
					}
				});

		registerForContextMenu(playerRoundsList);
	}
}
