package br.net.du.fscore.persist;

public class MatchPlayerKey {
	private long matchId;
	private long playerId;

	public MatchPlayerKey(long matchId, long playerId) {
		this.matchId = matchId;
		this.playerId = playerId;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof MatchPlayerKey)) {
			return false;
		}

		return this.matchId == ((MatchPlayerKey) other).getMatchId()
				&& this.playerId == ((MatchPlayerKey) other).getPlayerId();
	}
}
