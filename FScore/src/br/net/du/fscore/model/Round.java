package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Round implements Serializable, Comparable<Round> {

	private static final long serialVersionUID = 1L;

	private long id;
	int numberOfCards = 0;
	List<PlayerRound> playerRounds = null;

	public Round(int numberOfCards) {
		this.numberOfCards = numberOfCards;
		playerRounds = new ArrayList<PlayerRound>();
	}

	public int getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(int numberOfCards) {
		this.numberOfCards = numberOfCards;
	}

	public List<PlayerRound> getPlayerRounds() {
		return playerRounds;
	}

	public void addPlayerRound(PlayerRound playerRound) {
		this.playerRounds.add(playerRound);
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
	public int compareTo(Round other) {
		return new Integer(numberOfCards).compareTo(new Integer(other
				.getNumberOfCards()));
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Round)) {
			return false;
		}

		Round otherRound = (Round) other;
		if (this.numberOfCards == otherRound.getNumberOfCards()
				&& this.playerRounds.equals(otherRound.getPlayerRounds())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (playerRounds == null ? 0 : playerRounds.hashCode());
		hash = hash * 31 + numberOfCards;
		return hash;
	}

	public String toString() {
		return "Round " + String.valueOf(id);
	}
}
