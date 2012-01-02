package br.net.du.fodasescore.model;

public class PlayerRound {

	private Player player = null;
	private int bet = 0;
	private int wins = 0;

	public PlayerRound(Player player) {
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

}
