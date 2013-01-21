package org.ifcopenshell.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ifcopenshell.util.Utils;

public class IfcObject {
	protected final int id;
	protected final int parent_id;
	protected final String type;
	protected final String guid;
	protected final String name;
	protected float[] matrix;
	protected List<IfcObject> children;
	protected IfcObject parent;

	public IfcObject(int id, int parent_id, String type, String guid,
			String name, float[] matrix) {
		this.id = id;
		this.parent_id = parent_id;
		this.type = type;
		this.guid = guid;
		this.matrix = matrix;
		this.name = name;
		matrixTo4_4();
		children = new ArrayList<IfcObject>();
	}

	public String getType() {
		return type;
	}

	public String getGuid() {
		return guid;
	}

	public int getId() {
		return id;
	}

	public int getParent_id() {
		return parent_id;
	}

	public float[] getMatrix() {
		return matrix;
	}

	public IfcObject getParent() {
		return parent;
	}

	public void setParent(IfcObject parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	public String getName() {
		return name;
	}

	public List<IfcObject> getChildren() {
		return children;
	}

	protected void matrixTo4_4() {
		float mat[] = { matrix[0], matrix[1], matrix[2], 0, matrix[3],
				matrix[4], matrix[5], 0, matrix[6], matrix[7], matrix[8], 0,
				matrix[9], matrix[10], matrix[11], 1 };
		matrix = mat;
	}

	public void addChild(IfcObject object) {
		children.add(object);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"id\":").append("\"").append(id).append("\",");
		builder.append("\"pid\":").append("\"").append(parent_id).append("\",");
		builder.append("\"ty\":").append("\"").append(type).append("\",");
		builder.append("\"guid\":").append("\"").append(StringEscapeUtils.escapeJava(guid)).append("\",");
		builder.append("\"nm\":").append("\"").append(StringEscapeUtils.escapeJava(name)).append("\",");
		builder.append("\"mat\":").append(
				Utils.floatArrayToString(null, matrix));
		return builder.toString();
	}

}
