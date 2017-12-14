package com.roop.utils.lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by roop on 29.06.2015.
 */
public class ListSorter<T> {

	protected final Comparator<? super T> _comperator;

	public ListSorter(Comparator<? super T> comparator){
		_comperator = comparator;
	}

	/**
	 * Takes a generic object 'e' and adds it to the List 'l'.
	 * The correct position in the List is find with a binarysearch.
	 * The object will only be added if not already present.
	 * @param l
	 * @param e
	 * @return
	 */
	public boolean addIfAbsent(List<T> l, T e) {
		int i = Collections.binarySearch(l, e, _comperator);

		if(i < 0) {
			l.add((-i)-1, e);

			return true;
		}

		return false;
	}

	/**
	 * Takes a generic object 'e' and adds it to the List 'l'.
	 * The correct position in the List is find with a binarysearch.
	 * The object will only be added if not already present.
	 * @param l
	 * @param e
	 * @return
	 */
	public boolean add(List<T> l, T e) {
		int i = Collections.binarySearch(l, e, _comperator);

		if(i < 0) {
			l.add((-i)-1, e);
		} else {
			l.add(i, e);
		}

		return true;
	}

	public boolean addAll(List<T> l, Collection<? extends T> es) {
		if(es.size() < 1)
			return false;

		for (T e : es) {
			this.add(l, e);
		}

		return true;
	}

	public boolean addAll(List<T> l, T... es) {
		if(es.length < 1)
			return false;

		for (T e : es) {
			this.add(l, e);
		}

		return true;
	}

	/**
	 * Takes a generic object 'e' and adds it to the List 'l'.
	 * The correct position in the List is find with a binarysearch.
	 * The object will be added if not already present or override existing.
	 * @param l
	 * @param e
	 * @return
	 */
	public void put(List<T> l, T e){
		int i = Collections.binarySearch(l, e, _comperator);

		if(i < 0){
			l.add((-i)-1, e);
		} else{
			l.set(i, e);
		}
	}

	public boolean putAll(List<T> l, Collection<? extends T> es) {
		if(es.size() < 1)
			return false;

		for (T e : es) {
			this.put(l, e);
		}

		return true;
	}

	public boolean putAll(List<T> l, T... es) {
		if(es.length < 1)
			return false;

		for (T e : es) {
			this.put(l, e);
		}

		return true;
	}

	public void sort(List<T> l) {
		Collections.sort(l, _comperator);
	}

	public <E> int binarySearch(List<T> l, E o, Comparator<? super Object> c) {
		return Collections.binarySearch(l, o, c);
	}
}
