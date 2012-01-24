package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Round implements Serializable, Comparable<Round> {

	private static final long serialVersionUID = 1L;

	private long id;
	int numberOfCards = 0;
	List<PlayerRound> data = null;

	public Round() {
		data = new ArrayList<PlayerRound>();
	}

	public int getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(int numberOfCards) {
		this.numberOfCards = numberOfCards;
	}

	public List<PlayerRound> getData() {
		return data;
	}

	public void setData(List<PlayerRound> data) {
		this.data = data;
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
		if (this.equals(other)) {
			return 0;
		}

		// TODO: implement this false return properly
		return -1;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Round)) {
			return false;
		}

		Round otherRound = (Round) other;
		if (this.numberOfCards == otherRound.getNumberOfCards()
				&& this.data.equals(otherRound.getData())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (data == null ? 0 : data.hashCode());
		hash = hash * 31 + numberOfCards;
		return hash;
	}

	public String toString() {
		return "Round " + String.valueOf(id);
	}
}
