package br.net.du.fodasescore.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.net.du.fodasescore.R;
import br.net.du.fodasescore.model.Match;
import br.net.du.fodasescore.model.Player;
import br.net.du.fodasescore.model.Round;

public class RoundList extends Activity {
	private Match match;

	public static final int CONTACT_SELECTED_RESULT_ID = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roundlist);

		match = (Match) getIntent().getSerializableExtra("selectedMatch");

		List<Round> rounds = match.getRounds();
		final ListView roundList = (ListView) findViewById(R.id_roundlist.roundlist);
		ArrayAdapter<Round> adapter = new ArrayAdapter<Round>(this,
				android.R.layout.simple_list_item_1, rounds);
		roundList.setAdapter(adapter);

		roundList.setClickable(true);
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
}
