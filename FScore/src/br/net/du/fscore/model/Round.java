package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Round implements Serializable, Comparable<Round> {
	private static final long serialVersionUID = 1L;

	private long id = 0;
	private long numberOfCards = 0;
	private long matchId;
	private List<PlayerRound> playerRounds = null;

	public Round(long numberOfCards) {
		this.numberOfCards = numberOfCards;
		playerRounds = new ArrayList<PlayerRound>();
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public long getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(long numberOfCards) {
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
		return new Long(numberOfCards).compareTo(new Long(other
				.getNumberOfCards()));
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Round)) {
			return false;
		}

		Round otherRound = (Round) other;
		if (this.getId() == otherRound.getId()
				&& this.numberOfCards == otherRound.getNumberOfCards()
				&& this.matchId == otherRound.getMatchId()
				&& this.playerRounds.equals(otherRound.getPlayerRounds())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (int) id;
		hash = hash * 31 + (int) numberOfCards;
		hash = hash * 31 + (int) matchId;
		return hash;
	}

	public String toString() {
		return "Round - " + numberOfCards + " cards";
	}
}
