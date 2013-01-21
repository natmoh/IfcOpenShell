package org.ifcopenshell.model;

public class IfcGeometryObject extends IfcObject {

	private final IfcMesh mesh;

	public IfcGeometryObject(int id, int parent_id, IfcMesh mesh,
			float[] matrix, String type, String guid, String name) {
		super(id, parent_id, type, guid, name, matrix);
		this.mesh = mesh;
	}

	public int getId() {
		return id;
	}

	public int getParent_id() {
		return parent_id;
	}

	public IfcMesh getMesh() {
		return mesh;
	}

	public float[] getMatrix() {
		return matrix;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append(",");
		builder.append("\"geo\":{").append(mesh).append("}");
		builder.append("}");
		return builder.toString();
	}

}
