package com.roop.utils.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project: roop-utils
 * User: roop
 * Date: 15.03.2017
 * Time: 23:11
 * Copyright: Ralf Wiedemann
 */
public class ListManager<T> extends ListSorter<T> {

	public ListManager(Comparator<? super T> comparator) {
		super(comparator);
	}

	public int find(List<T> l, T o) {
		return Collections.binarySearch(l, o, super._comperator);
	}

	public <E> int find(List<T> l, E o, Comparator<? super Object> c) {
		return super.binarySearch(l, o, c);
	}

	public <E> T findAndGet(List<T> l, E o, Comparator<? super Object> c) {
		int pos = binarySearch(l, o, c);
		if(pos < 0)
			return null;
		return l.get(pos);
	}

	public T findAndGet(List<T> l, T o) {
		int pos = this.find(l, o);
		if(pos < 0)
			return null;
		return l.get(pos);
	}
}
