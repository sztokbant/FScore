package br.net.du.fscore.model;

public class PlayerRound implements Comparable<PlayerRound> {

	private Player player = null;
	private int bet = 0;
	private int wins = 0;

	public PlayerRound(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	@Override
	public int compareTo(PlayerRound other) {
		if (this.equals(other)) {
			return 0;
		}

		// TODO: implement this false return properly
		return -1;
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
		// TODO implement it properly
		return 0;
	}

	public String toString() {
		return "a PlayerRound";
	}

}
