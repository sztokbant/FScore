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

	public long getMaxCardsPerRound() {
		return 51 / players.size();
	}

	public long getNumberOfCardsSuggestion() {
		if (rounds.isEmpty()) {
			return 1;
		}

		long lastRoundCardsPerPlayer = rounds.get(rounds.size() - 1)
				.getNumberOfCards();

		if (rounds.size() >= getMaxCardsPerRound()) {
			return lastRoundCardsPerPlayer - 1;
		}

		return lastRoundCardsPerPlayer + 1;
	}

	public Match with(Player player) throws IllegalArgumentException,
			IllegalStateException {
		if (players.size() >= 51) {
			throw new IllegalStateException(
					"Maximmum number of players reached");
		}

		if (player == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}

		if (!rounds.isEmpty()) {
			throw new IllegalStateException(
					"Cannot add players after the match has started");
		}

		for (Player p : players) {
			if (p.equals(player)) {
				return this;
			}
		}

		players.add(player);
		return this;
	}

	public boolean deletePlayer(Player player) throws IllegalStateException {
		if (!rounds.isEmpty()) {
			throw new IllegalStateException(
					"Cannot delete players after the match has started");
		}

		if (!players.contains(player)) {
			return false;
		}

		players.remove(player);

		return true;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Round newRound() throws IllegalStateException {
		if (!rounds.isEmpty() && !rounds.get(rounds.size() - 1).isComplete()) {
			throw new IllegalStateException("Last round is incomplete.");
		}

		long nextRoundsCards = getNumberOfCardsSuggestion();
		if (nextRoundsCards == 0) {
			throw new IllegalStateException("Match is over.");
		}

		return this.newRound(nextRoundsCards);
	}

	Round newRound(long numberOfCards) throws IllegalArgumentException,
			IllegalStateException {
		if (players.size() < 2) {
			throw new IllegalStateException(
					"Match must have at least 2 players to begin");
		}

		if (numberOfCards < 1 || numberOfCards > getMaxCardsPerRound()) {
			throw new IllegalArgumentException("numberOfCards must be between "
					+ String.valueOf(1) + " and " + getMaxCardsPerRound());
		}

		Round round = new Round(numberOfCards);

		for (Player player : players) {
			PlayerRound playerRound = new PlayerRound(player);
			round.addPlayerRound(playerRound);
		}

		round.setMatchId(this.getId());
		rounds.add(round);

		return round;
	}

	public Match addRound(Round round) throws IllegalArgumentException,
			IllegalStateException {
		if (round == null) {
			throw new IllegalArgumentException("Round cannot be null");
		}

		if (players.size() < 2) {
			throw new IllegalStateException(
					"Match must have at least 2 players to begin");
		}

		round.setMatchId(this.getId());
		rounds.add(round);

		return this;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public List<PlayerScore> getPlayerScores() {
		List<PlayerScore> scores = new ArrayList<PlayerScore>();

		for (Player player : players) {
			long score = 0L;

			for (Round round : rounds) {
				for (PlayerRound playerRound : round.getPlayerRounds()) {
					Player prPlayer = playerRound.getPlayer();
					if (player.equals(prPlayer)) {
						score += playerRound.getScore();
					}
				}
			}

			scores.add(new PlayerScore(player, score));
		}

		return scores;
	}

	public boolean hasPlayer(String name) {
		Player player = new Player(name);
		return this.players.contains(player);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
		for (Round round : rounds) {
			round.setMatchId(id);
		}
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

	@Override
	public String toString() {
		return name + DateFormat.format(" (dd-MM-yyyy hh:mm)", date);
	}
}
