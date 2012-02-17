package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.DateFormat;

public class Match implements Serializable, Comparable<Match> {
	private static final long serialVersionUID = 1L;

	private long id = 0;
	private String name = "";
	private Calendar date;

	List<Player> players = new ArrayList<Player>();
	List<Round> rounds = new ArrayList<Round>();

	public Match(String name) {
		this.name = name;
		date = Calendar.getInstance();
	}

	public Match() {
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

	public Match withPlayer(Player player) throws IllegalArgumentException {
		if (player == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}

		for (Player p : players) {
			if (p.equals(player)) {
				return this;
			}
		}

		players.add(player);
		return this;
	}

	public boolean deletePlayer(Player player) {
		if (!players.contains(player)) {
			return false;
		}

		for (Round round : rounds) {
			for (PlayerRound playerRound : round.getPlayerRounds()) {
				if (playerRound.getPlayer().equals(player)) {
					return false;
				}
			}
		}

		players.remove(player);

		return true;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addRound(Round round) throws IllegalArgumentException {
		if (round == null) {
			throw new IllegalArgumentException("Round cannot be null");
		}

		round.setMatchId(this.getId());
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

	public boolean isPersistent() {
		return this.getId() != 0;
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

		Match otherMatch = (Match) other;

		if (this.getId() != otherMatch.getId()
				|| !this.name.equals(otherMatch.getName())
				|| !this.date.equals(otherMatch.getDate())
				|| !this.players.equals(otherMatch.getPlayers())
				|| !this.rounds.equals(otherMatch.getRounds())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (int) id;
		hash = hash * 31 + (name == null ? 0 : name.hashCode());
		hash = hash * 31 + (date == null ? 0 : (int) date.getTimeInMillis());
		return hash;
	}

	public String toString() {
		return name + DateFormat.format(" (dd-MM-yyyy hh:mm:ss)", date);
	}
}
