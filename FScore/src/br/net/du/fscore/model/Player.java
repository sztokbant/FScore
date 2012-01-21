package br.net.du.fscore.model;

import java.io.Serializable;

public class Player implements Serializable, Comparable<Player> {
	private long id;
	private String name;

	public Player() {
		name = "";
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

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Player)) {
			return false;
		}

		return this.compareTo((Player) other) == 0;
	}

	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (name == null ? 0 : name.hashCode());
		return hash;
	}

	public boolean isPersistent() {
		return this.getId() != 0;
	}
}
