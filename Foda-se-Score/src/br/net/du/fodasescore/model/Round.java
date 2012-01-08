package br.net.du.fodasescore.model;

import java.io.Serializable;
import java.util.List;

public class Round implements Serializable {

	private static final long serialVersionUID = 1L;

	int numberOfCards = 0;
	List<PlayerRound> data = null;

	public Round() {
	}
}
