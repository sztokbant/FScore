package br.net.du.fscore.persist.dao;

public interface Dao<T> {
	long save(T type);

	void update(T type);

	void delete(T type);
}
