package br.net.du.fscore.model;

import java.io.Serializable;

public class PlayerRound implements Serializable, Comparable<PlayerRound> {
	public static final long EMPTY = -1;
	private static final long serialVersionUID = 1L;

	private long id = 0;
	private long bet = EMPTY;
	private long wins = EMPTY;
	private Player player = null;
	private long roundId = 0;

	public PlayerRound(Player player) {
		this.player = player;
	}

	public long getBet() {
		return bet;
	}

	public PlayerRound setBet(long bet) {
		this.bet = bet;
		return this;
	}

	public long getWins() {
		return wins;
	}

	public PlayerRound setWins(long wins) {
		this.wins = wins;
		return this;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getRoundId() {
		return roundId;
	}

	public void setRoundId(long roundId) {
		this.roundId = roundId;
	}

	public long getScore() {
		if (bet == EMPTY || wins == EMPTY) {
			return 0;
		}

		long score = wins;
		score += (wins == bet) ? 5 : 0;

		return score;
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
	public int compareTo(PlayerRound other) {
		Long myScore = this.getScore();
		Long otherScore = other.getScore();

		if (myScore.equals(otherScore)) {
			return this.player.compareTo(other.getPlayer());
		}

		return (-1) * myScore.compareTo(otherScore);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof PlayerRound)) {
			return false;
		}

		PlayerRound otherPlayerRound = (PlayerRound) other;
		if (this.getId() != otherPlayerRound.getId()
				|| this.getRoundId() != otherPlayerRound.getRoundId()
				|| this.getBet() != otherPlayerRound.getBet()
				|| this.getWins() != otherPlayerRound.getWins()
				|| !this.player.equals(otherPlayerRound.getPlayer())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (int) id;
		hash = hash * 31 + (int) roundId;
		hash = hash * 31 + (int) bet;
		hash = hash * 31 + (int) wins;
		hash = hash * 31 + (player == null ? 0 : player.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(player.toString() + " [" + getScore() + "] (");
		sb.append((wins == EMPTY) ? "-" : wins);
		sb.append("/");
		sb.append((bet == EMPTY) ? "-" : bet);
		sb.append(")");
		return sb.toString();
	}
}
