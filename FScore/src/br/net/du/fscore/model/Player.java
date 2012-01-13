package br.net.du.fscore.model;

public class Player implements Comparable<Player> {
	private long id;
	private String name = null;

	public Player(String name) {
		this.name = name;
	}

	public boolean isPersistent() {
		return getId() != 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Player other) {
		return this.name.compareTo(other.getName());
	}
}
