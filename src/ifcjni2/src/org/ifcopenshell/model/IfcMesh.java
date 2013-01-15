package org.ifcopenshell.model;

import org.ifcopenshell.util.Utils;

public final class IfcMesh {

	private final int[] indices;
	private final float[] vertices;

	public IfcMesh(int[] indices, float[] vertices) {
		this.indices = indices;
		this.vertices = vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public float[] getVertices() {
		return vertices;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"mesh\":{" + Utils.intArrayToString("f", indices)
				+ ",");
		builder.append(Utils.floatArrayToString("v", vertices) + ",");
		return builder.toString();
	}

}
