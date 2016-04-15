package com.roop.utils.lists;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

/**
 * Created by roop on 29.06.2015.
 */
public class SortedList<T> implements List<T> {

	private final List<T> _data = new ArrayList<>();
	@XmlTransient
	private Comparator<? super T> _comperator;

	public SortedList(){
		_comperator = COMP;
	}
	public SortedList(Comparator<? super T> comparator){
		_comperator = comparator;
	}

	public void setComperator(Comparator<? super T> comp) {
		this._comperator = comp;
		_data.sort(comp);
	}

	public boolean isDefaultComperator(){
		return _comperator == COMP;
	}

	public Comparator<? super T> getComperator() {
		return _comperator;
	}

	public static Comparator<Object> getCOMP() {
		return COMP;
	}

	@Override
	public int size() {
		return _data.size();
	}

	@Override
	public boolean isEmpty() {
		return _data.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return _data.iterator();
	}

	@Override
	public Object[] toArray() {
		return _data.toArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> E[] toArray(E[] a) {
		return _data.toArray(a);
	}

	public boolean addIfAbsent(T t){
		int i = Collections.binarySearch(_data, t, _comperator);

		if(i < 0){
			_data.add((-i)-1, t);

			return true;
		}

		return false;
	}

	@Override
	public boolean add(T t) {
		int i = Collections.binarySearch(_data, t, _comperator);

		if(i < 0) {
			_data.add((-i)-1, t);

			return true;
		} else{
			_data.add(i, t);
		}

		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> es) {
		if(es.size() < 1)
			return false;

		for (T e : es) {
			this.add(e);
		}

		return true;
	}

	public boolean addAll(T... es) {
		if(es.length < 1)
			return false;

		for (T e : es) {
			this.add(e);
		}

		return true;
	}

	/**
	 *
	 * @param t
	 * @return true if t is newly added
	 */
	public boolean put(T t){
		int i = Collections.binarySearch(_data, t, _comperator);

		if(i < 0){
			_data.add((-i)-1, t);
			return true;
		} else{
			_data.set(i, t);
			return false;
		}
	}

	public boolean putAll(Collection<? extends T> es) {
		if(es.size() < 1)
			return false;

		for (T e : es) {
			this.put(e);
		}

		return true;
	}

	public boolean putAll(T... es) {
		if(es.length < 1)
			return false;

		for (T e : es) {
			this.put(e);
		}

		return true;
	}

	@Override
	public boolean remove(Object o) {
		return _data.remove(o);
	}

	@Override
	public T remove(int index) {
		return _data.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return _data.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return _data.retainAll(c);
	}

	@Override
	public void clear() {
		_data.clear();
	}

	public T get(int index) {
		return _data.get(index);
	}

	/**
	 * Optimize contains, containsAll, indexOf and lastIndexOf.
	 * indexOf and lastIndexOf are treated the same.
	 * indexes should be updated with better code.
	 *
	*/

	@Override
	public boolean contains(Object o) {
		try{
			return Collections.binarySearch(_data, (T)o, _comperator) >= 0;
		} catch (ClassCastException e){}

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if(!contains(o))
				return false;
		}

		return true;
	}

	@Override
	public int indexOf(Object o) {
		try{
			return Collections.binarySearch(_data, (T)o, _comperator);
		} catch (ClassCastException e){}

		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return indexOf(o);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return _data.subList(fromIndex, toIndex);
	}

	/*
	public <E> int binarySearch(E o, Comparator<? super Object> c) {
		return Collections.binarySearch(_data, o, c);
	}
	*/

	/**
	 * These Methods are not implemented because they could harm the order of _data;
	 *
	 */

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new NotImplementedException();
	}

	@Override
	public T set(int index, T element) {
		throw new NotImplementedException();
	}

	@Override
	public void add(int index, T element) {
		throw new NotImplementedException();
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new NotImplementedException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new NotImplementedException();
	}

	private static final Comparator<Object> COMP = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			return Integer.compare(o1.hashCode(), o2.hashCode());
		}

		@Override
		public String toString() {
			return "SortedList.default.comperator";
		}
	};
}
