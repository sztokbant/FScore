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

	public boolean equals(Player other) {
		return this.compareTo(other) == 0;
	}

	public String toString() {
		return name;
	}

	public boolean isPersistent() {
		return getId() != 0;
	}
}
