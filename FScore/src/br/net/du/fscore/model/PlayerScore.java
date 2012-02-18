package br.net.du.fscore.model;

public class PlayerScore implements Comparable<PlayerScore> {

	private Player player;
	private long score;

	public PlayerScore(Player player, long score) {
		this.player = player;
		this.score = score;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	@Override
	public int compareTo(PlayerScore other) {
		if (this.score == other.getScore()) {
			return player.compareTo(other.getPlayer());
		}

		return (-1) * (new Long(score).compareTo(new Long(other.getScore())));
	}

	@Override
	public String toString() {
		return player.toString() + " [" + score + "]";
	}
}
