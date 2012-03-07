package br.net.du.fscore.model;

import java.io.Serializable;

public class Player implements Serializable, Comparable<Player> {
	private static final long serialVersionUID = 1L;

	private long id = 0;
	private String name;

	public Player(String name) throws IllegalArgumentException {
		name = name.trim();

		if (name.equals("")) {
			throw new IllegalArgumentException("Name cannot be empty.");
		}

		this.setName(capitalizeFirstLetter(name));
	}

	private String capitalizeFirstLetter(String name) {
		return String.format("%s%s", Character.toUpperCase(name.charAt(0)),
				name.substring(1));
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

	public boolean isPersistent() {
		return this.getId() != 0;
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

		return this.name.equalsIgnoreCase(((Player) other).getName());
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (name == null ? 0 : name.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		return name;
	}
}
