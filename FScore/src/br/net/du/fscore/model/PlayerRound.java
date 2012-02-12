package br.net.du.fscore.model;

import java.io.Serializable;

public class PlayerRound implements Serializable, Comparable<PlayerRound> {
	private static final long serialVersionUID = 1L;

	private long id = 0;
	private long bet = 0;
	private long wins = 0;
	private Player player = null;
	private long roundId = 0;

	public PlayerRound(Player player) {
		this.player = player;
	}

	public long getBet() {
		return bet;
	}

	public void setBet(long bet) {
		this.bet = bet;
	}

	public long getWins() {
		return wins;
	}

	public void setWins(long wins) {
		this.wins = wins;
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
		return this.player.compareTo(other.getPlayer());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof PlayerRound)) {
			return false;
		}

		PlayerRound otherPlayerRound = (PlayerRound) other;
		if (this.getBet() != otherPlayerRound.getBet()
				|| this.getWins() != otherPlayerRound.getWins()
				|| !this.player.equals(otherPlayerRound.getPlayer())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (int) bet;
		hash = hash * 31 + (int) wins;
		hash = hash * 31 + (player == null ? 0 : player.hashCode());
		return hash;
	}

	public String toString() {
		return "PlayerRound: " + player.toString() + " [" + bet + ", " + wins
				+ "]";
	}
}
