package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Round implements Serializable, Comparable<Round> {
	private static final long serialVersionUID = 1L;

	public static final long NO_FORBIDDEN_BET = -1;

	private long id = 0;
	private long numberOfCards = 0;
	private long matchId;
	private List<PlayerRound> playerRounds = null;

	public Round(long numberOfCards) throws IllegalArgumentException {
		this.setNumberOfCards(numberOfCards);
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

	public void setNumberOfCards(long numberOfCards)
			throws IllegalArgumentException {
		if (numberOfCards <= 0) {
			throw new IllegalArgumentException(
					"Number of cards must be greater than 0");
		}

		this.numberOfCards = numberOfCards;
	}

	public List<PlayerRound> getPlayerRounds() {
		return playerRounds;
	}

	public Round addPlayerRound(PlayerRound playerRound)
			throws IllegalStateException, IllegalArgumentException {
		if (playerRound == null) {
			throw new IllegalArgumentException("playerRound cannot be null");
		}

		for (PlayerRound pr : playerRounds) {
			if (pr.getPlayer().equals(playerRound.getPlayer())) {
				throw new IllegalStateException("Player "
						+ playerRound.getPlayer().getName()
						+ " already in round");
			}
		}

		this.playerRounds.add(playerRound);

		return this;
	}

	public void setBet(Player player, long bet) {
		PlayerRound selectedPlayerRound = null;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getPlayer().equals(player)) {
				selectedPlayerRound = playerRound;
				break;
			}
		}

		if (selectedPlayerRound == null) {
			throw new IllegalArgumentException("Player not found!");
		}

		if (bet > numberOfCards) {
			throw new IllegalArgumentException("Bet must be between 0 and "
					+ numberOfCards);
		}

		if (bet == getForbiddenBet(selectedPlayerRound)) {
			throw new IllegalArgumentException("Sorry, your bed cannot be "
					+ bet);
		}

		selectedPlayerRound.setBet(bet);
	}

	public long getForbiddenBet(PlayerRound selectedPlayerRound) {
		if (!isLastPlayerToBet()) {
			return NO_FORBIDDEN_BET;
		}

		long betTotal = 0;
		for (PlayerRound playerRound : playerRounds) {
			if (playerRound == selectedPlayerRound) {
				continue;
			}

			if (playerRound.getBet() != PlayerRound.EMPTY) {
				betTotal += playerRound.getBet();
			}
		}

		long forbiddenBet = numberOfCards - betTotal;
		if (forbiddenBet >= 0) {
			return forbiddenBet;
		}

		return NO_FORBIDDEN_BET;
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

	@Override
	public String toString() {
		return "Round - " + numberOfCards + " cards";
	}

	private boolean isLastPlayerToBet() {
		long betCount = 0;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getBet() != PlayerRound.EMPTY) {
				betCount++;
			}
		}

		if (betCount < playerRounds.size() - 1) {
			return false;
		}

		return true;
	}
}
