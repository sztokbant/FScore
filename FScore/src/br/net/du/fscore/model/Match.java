package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Match implements Serializable {

	private static final long serialVersionUID = 1L;

	Set<Player> players = new HashSet<Player>();
	List<Round> rounds = new ArrayList<Round>();

	public Match() {
	}

	public Match withPlayer(Player player) {
		players.add(player);
		return this;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public void addRound(Round round) {
		rounds.add(round);
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public String toString() {
		return "this is a Match";
	}

	public List<Player> getPlayersAsList() {
		List<Player> players = new ArrayList<Player>();
		for (Player p : getPlayers()) {
			players.add(p);
		}
		return players;
	}
}
