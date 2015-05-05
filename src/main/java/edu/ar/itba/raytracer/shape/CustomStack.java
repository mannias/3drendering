package edu.ar.itba.raytracer.shape;

import edu.ar.itba.raytracer.KdTree.KdNode;
import edu.ar.itba.raytracer.KdTree.StackElement;

public class CustomStack {

	private static final int MAX_SIZE = 10000;

	private final StackElement[] array = new StackElement[MAX_SIZE];
	public int top;
	
	public CustomStack() {
		for (int i = 0; i < array.length; i++) {
			array[i] = new StackElement();
		}
	}

	public void push(final KdNode node, final double start,
			final double end) {
		try {
			final StackElement elem = array[top++];
			elem.node = node;
			elem.start = start;
			elem.end = end;
		} catch (ArrayIndexOutOfBoundsException e) {
			// Ignore.
		}
	}

	public StackElement pop() {
		try {
			return array[--top];
		} catch (ArrayIndexOutOfBoundsException e) {
			top = 0;
			return null;
		}
	}

	public void reset() {
		top = 0;
	}

}
