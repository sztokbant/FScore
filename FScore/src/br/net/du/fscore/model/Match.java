package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.DateFormat;

public class Match implements Serializable, Comparable<Match> {

	private static final long serialVersionUID = 1L;

	List<Player> players = new ArrayList<Player>();
	List<Round> rounds = new ArrayList<Round>();
	Calendar date;

	public Match() {
		date = Calendar.getInstance();
	}

	public Match withPlayer(Player player) {
		for (Player p : players) {
			if (p.equals(player)) {
				return this;
			}
		}
		players.add(player);
		return this;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addRound(Round round) {
		rounds.add(round);
	}

	public List<Round> getRounds() {
		return rounds;
	}

	private Calendar getDate() {
		return date;
	}

	public String toString() {
		return "Foda-Se Match " + DateFormat.format("dd-MM-yyyy", date);
	}

	@Override
	public int compareTo(Match other) {
		return this.date.compareTo(other.getDate());
	}
}
