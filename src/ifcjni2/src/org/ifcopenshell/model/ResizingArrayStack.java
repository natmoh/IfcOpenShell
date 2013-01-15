package org.ifcopenshell.model;

import java.util.Iterator;

public class ResizingArrayStack<E> implements Iterable<E> {

	private int N;
	private E data[];

	public ResizingArrayStack() {
		this(1);
	}

	public ResizingArrayStack(int capacity) {
		data = (E[]) new Object[capacity];
	}

	public boolean contains(E elem) {
		for (E e : data) {
			if (elem.equals(e))
				return true;
		}
		return false;
	}

	public void push(E e) {
		if (N == data.length)
			resizing(N * 2);
		data[N++] = e;
		printStatus("push");
	}

	public E pop() {
		if (!isEmpty()) {
			E e = data[--N];
			data[N] = null;
			if (N > 0 && N == data.length / 4)
				resizing(data.length / 2);

			printStatus("pop");
			return e;
		} else
			return null;

	}

	public int size() {
		return N;
	}

	public boolean isEmpty() {
		return N == 0;
	}

	private void resizing(int max) {
		E temp[] = (E[]) new Object[max];
		for (int i = 0; i < N; i++)
			temp[i] = data[i];

		data = temp;
	}

	private void printStatus(String msg) {
		// System.out.println(msg + " N: " + N + " L: " + data.length);
	}

	public Iterator<E> iterator() {
		return new ResizingArrayIterator();
	}

	private class ResizingArrayIterator implements Iterator<E> {

		private int i = N;

		public boolean hasNext() {
			return i > 0;
		}

		public E next() {
			return data[--i];
		}

		public void remove() {

		}

	}

}
