package br.net.du.fscore.model.exceptions;

public class FScoreException extends Exception {

	private static final long serialVersionUID = 1L;

	private final int messageId;

	public FScoreException(int messageId) {
		this.messageId = messageId;
	}

	public int getMessageId() {
		return messageId;
	}
}
