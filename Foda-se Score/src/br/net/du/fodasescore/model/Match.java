package br.net.du.fodasescore.model;

import java.util.ArrayList;
import java.util.List;

public class Match {

	List<Player> players = new ArrayList<Player>();
	List<Round> rounds = new ArrayList<Round>();

	public Match() {
	}

	public Match withPlayer(Player player) {
		players.add(player);
		return this;
	}

	public void addRound(Round round) {
		rounds.add(round);
	}
}
