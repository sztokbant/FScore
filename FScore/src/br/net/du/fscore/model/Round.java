package br.net.du.fscore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.net.du.fscore.R;
import br.net.du.fscore.model.exceptions.FScoreException;

public class Round implements Serializable, Comparable<Round> {
	private static final long serialVersionUID = 1L;

	public static final long NO_FORBIDDEN_BET = -1;

	private long id = 0;
	private long numberOfCards = 0;
	private long matchId;
	private List<PlayerRound> playerRounds = null;

	public Round(long numberOfCards) throws FScoreException {
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

	public void setNumberOfCards(long numberOfCards) throws FScoreException {
		if (numberOfCards <= 0) {
			throw new FScoreException(
					R.string.num_of_cards_must_be_greater_than_0);
		}

		this.numberOfCards = numberOfCards;
	}

	public List<PlayerRound> getPlayerRounds() {
		return playerRounds;
	}

	public Round addPlayerRound(PlayerRound playerRound) throws FScoreException {
		if (playerRound == null) {
			throw new FScoreException(R.string.playerround_cannot_be_null);
		}

		for (PlayerRound pr : playerRounds) {
			if (pr.getPlayer().equals(playerRound.getPlayer())) {
				throw new FScoreException(R.string.player_already_in_this_round);
			}
		}

		this.playerRounds.add(playerRound);

		return this;
	}

	public void setBet(Player player, long bet) throws FScoreException {
		PlayerRound selectedPlayerRound = getPlayerRound(player);

		if (bet < 0 || bet > numberOfCards) {
			throw new FScoreException(
					R.string.bet_must_be_between_0_and_rounds_cards);
		}

		if (bet == getForbiddenBet(player)) {
			throw new FScoreException(R.string.msg_your_bet_cannot_be);
		}

		selectedPlayerRound.setBet(bet);
	}

	public void setWins(Player player, long wins) throws FScoreException {
		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getPlayer().equals(player)) {
				continue;
			}

			if (playerRound.getBet() == PlayerRound.EMPTY) {
				throw new FScoreException(
						R.string.cannot_set_wins_before_all_bets);
			}
		}

		PlayerRound selectedPlayerRound = getPlayerRound(player);

		if (wins < 0 || wins > numberOfCards) {
			throw new FScoreException(
					R.string.wins_must_be_between_0_and_rounds_cards);
		}

		if (!isAllowedWins(player, wins)) {
			throw new FScoreException(
					R.string.total_wins_must_be_equal_rounds_cards);
		}

		selectedPlayerRound.setWins(wins);
	}

	public void setBetRelaxed(Player player, long bet) throws FScoreException {
		PlayerRound selectedPlayerRound = getPlayerRound(player);
		selectedPlayerRound.setBet(bet);
	}

	public void setWinsRelaxed(Player player, long wins) throws FScoreException {
		PlayerRound selectedPlayerRound = getPlayerRound(player);
		selectedPlayerRound.setWins(wins);
	}

	public void validateBets() throws FScoreException {
		long totalBets = 0;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getBet() < 0
					|| playerRound.getBet() > numberOfCards) {
				throw new FScoreException(
						R.string.bet_must_be_between_0_and_rounds_cards);
			}

			totalBets += playerRound.getBet();
		}

		if (totalBets == numberOfCards) {
			throw new FScoreException(R.string.msg_your_bet_cannot_be);
		}
	}

	public void validateWins() throws FScoreException {
		long totalWins = 0;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getWins() < 0
					|| playerRound.getWins() > numberOfCards) {
				throw new FScoreException(
						R.string.wins_must_be_between_0_and_rounds_cards);
			}

			totalWins += playerRound.getWins();
		}

		if (totalWins != numberOfCards) {
			throw new FScoreException(
					R.string.total_wins_must_be_equal_rounds_cards);
		}
	}

	private boolean isAllowedWins(Player player, long wins)
			throws FScoreException {
		PlayerRound selectedPlayerRound = getPlayerRound(player);

		long winsSum = wins;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound == selectedPlayerRound
					|| playerRound.getWins() == PlayerRound.EMPTY) {
				continue;
			}

			winsSum += playerRound.getWins();
		}

		if (winsSum == numberOfCards
				|| (winsSum < numberOfCards && !isLastPlayerToWin())) {
			return true;
		}

		return false;
	}

	private PlayerRound getPlayerRound(Player player) throws FScoreException {
		PlayerRound selectedPlayerRound = null;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getPlayer().equals(player)) {
				selectedPlayerRound = playerRound;
				break;
			}
		}

		if (selectedPlayerRound == null) {
			throw new FScoreException(R.string.player_not_found);
		}

		return selectedPlayerRound;
	}

	public long getForbiddenBet(Player player) throws FScoreException {
		if (!isLastPlayerToBet()) {
			return NO_FORBIDDEN_BET;
		}

		PlayerRound selectedPlayerRound = getPlayerRound(player);

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

	public boolean hasAllBets() {
		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getBet() == PlayerRound.EMPTY) {
				return false;
			}
		}

		return true;
	}

	public boolean hasAnyWins() {
		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getWins() != PlayerRound.EMPTY) {
				return true;
			}
		}

		return false;
	}

	public boolean isComplete() {
		if (!hasAllBets()) {
			return false;
		}

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getWins() == PlayerRound.EMPTY) {
				return false;
			}
		}

		return true;
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
		StringBuilder sb = new StringBuilder();
		sb.append("Round - ");
		sb.append(numberOfCards);
		sb.append(numberOfCards > 1 ? " cards" : " card");
		return sb.toString();
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

	private boolean isLastPlayerToWin() {
		long winsCount = 0;

		for (PlayerRound playerRound : playerRounds) {
			if (playerRound.getWins() != PlayerRound.EMPTY) {
				winsCount++;
			}
		}

		if (winsCount < playerRounds.size() - 1) {
			return false;
		}

		return true;
	}
}
