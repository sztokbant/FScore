package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.DateFormat;

public class Match implements Serializable, Comparable<Match> {
	private long id;

	private static final long serialVersionUID = 1L;

	private String name;
	private Calendar date;

	List<Player> players = new ArrayList<Player>();
	List<Round> rounds = new ArrayList<Round>();

	public Match() {
		name = "";
		date = Calendar.getInstance();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String toString() {
		return name + DateFormat.format(" (dd-MM-yyyy hh:mm:ss)", date);
	}

	@Override
	public int compareTo(Match other) {
		return this.date.compareTo(other.getDate());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Match)) {
			return false;
		}

		return this.name == ((Match) other).getName()
				&& this.date == ((Match) other).getDate();
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (name == null ? 0 : name.hashCode());
		hash = hash * 31 + (date == null ? 0 : date.hashCode());
		return hash;
	}
}
