package com.langram.utils.channels;

import java.util.ArrayList;

/**
 * This file is part of the project java.
 *
 * @author Guillaume
 * @version 1.0
 * @date 04/12/2017
 * @since 1.0
 */
public interface RepositoryInterface<E> {

	void store(E e);
	ArrayList<E> retrieveAll();
}
