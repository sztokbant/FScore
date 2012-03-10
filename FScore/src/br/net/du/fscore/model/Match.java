package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.DateFormat;
import br.net.du.fscore.R;
import br.net.du.fscore.model.exceptions.FScoreException;

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

	private long getNumberOfCardsSuggestion() {
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

	public Match with(Player player) throws FScoreException {
		if (players.size() >= 51) {
			throw new FScoreException(R.string.max_num_of_players_reached);
		}

		if (player == null) {
			throw new FScoreException(R.string.player_cannot_be_null);
		}

		if (this.players.contains(player)) {
			throw new FScoreException(R.string.player_already_in_this_match);
		}

		if (!rounds.isEmpty()) {
			throw new FScoreException(
					R.string.cannot_add_players_after_match_started);
		}

		players.add(player);
		return this;
	}

	public boolean deletePlayer(Player player) throws FScoreException {
		if (!rounds.isEmpty()) {
			throw new FScoreException(
					R.string.cannot_delete_players_after_match_started);
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

	public Round newRound() throws FScoreException {
		if (!rounds.isEmpty() && !rounds.get(rounds.size() - 1).isComplete()) {
			throw new FScoreException(R.string.last_round_incomplete);
		}

		long nextRoundsCards = getNumberOfCardsSuggestion();
		if (nextRoundsCards == 0) {
			throw new FScoreException(R.string.match_over);
		}

		return this.newRound(nextRoundsCards);
	}

	Round newRound(long numberOfCards) throws FScoreException {
		if (players.size() < 2) {
			throw new FScoreException(
					R.string.match_must_have_at_least_2_players);
		}

		if (numberOfCards < 1 || numberOfCards > getMaxCardsPerRound()) {
			throw new FScoreException(
					R.string.num_of_cards_must_be_between_1_and_max);
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

	public Match addRound(Round round) throws FScoreException {
		if (round == null) {
			throw new FScoreException(R.string.round_cannot_be_null);
		}

		if (players.size() < 2) {
			throw new FScoreException(
					R.string.match_must_have_at_least_2_players);
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

	public String getFormattedWhen() {
		return DateFormat.format("dd-MM-yyyy hh:mm", date).toString();
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
		return name + " (" + getFormattedWhen() + ")";
	}
}
