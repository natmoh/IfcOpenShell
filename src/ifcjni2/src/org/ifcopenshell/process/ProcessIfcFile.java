package org.ifcopenshell.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ifcopenshell.jni.Library;
import org.ifcopenshell.model.IfcGeometryObject;
import org.ifcopenshell.model.IfcObject;
import org.ifcopenshell.model.ResizingArrayStack;
import org.ifcopenshell.util.HashCodeUtil;

public class ProcessIfcFile {

	private Library library;
	private Map<Integer, IfcObject> idToObject;
	private ResizingArrayStack<ParentChildHolder> idToParent;

	private IfcObject root;
	private PrintWriter writer;

	public ProcessIfcFile(String libpath) throws Exception {
		library = new Library(libpath);
		idToObject = new HashMap<Integer, IfcObject>();
		idToParent = new ResizingArrayStack<ParentChildHolder>();

	}

	public void parse(String fname) {
		File file = new File(fname);
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			int dlength = 0;
			int len = -1;
			byte[] buffer = new byte[4096];
			while ((len = stream.read(buffer)) != -1) {
				System.arraycopy(buffer, 0, data, dlength, len);
				dlength += len;
			}
			library.setIfcData(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
				stream = null;
			}
		}
	}

	public void parseFile(String fname) {
		File file = new File(fname);
		System.out.println(library.setIfcPath(file.getAbsolutePath()));
	}

	public void parseGeometry() {
		while (true) {
			IfcGeometryObject geometryObject = library.getIfcGeometry();
			if (geometryObject == null)
				break;

			if (!idToObject.containsKey(geometryObject.getId())) {
				idToObject.put(geometryObject.getId(), geometryObject);
			} else {
				System.out.println("Already present " + geometryObject.getId());
			}

			ParentChildHolder holder = new ParentChildHolder(
					geometryObject.getId(), geometryObject.getParent_id());
			if (!idToParent.contains(holder)) {
				idToParent.push(holder);
			} else {
				System.out.println("Already has parent: "
						+ geometryObject.getId());
			}

			// System.out.println(geometryObject);

		}

		System.out.println("Done");
	}

	public void processHierarchy() {
		while (!idToParent.isEmpty()) {
			ParentChildHolder holder = idToParent.pop();
			IfcObject parent = idToObject.get(holder.pid);
			if (parent == null) {
				parent = library.getObject(holder.pid);
				if (parent.getParent_id() > 0) {
					ParentChildHolder childHolder = new ParentChildHolder(
							parent.getId(), parent.getParent_id());
					idToParent.push(childHolder);

					if (!idToObject.containsKey(parent.getId())) {
						idToObject.put(parent.getId(), parent);
					} else {
						System.out.println("Already present " + parent.getId());
					}
				} else {
					root = parent;
				}
				/*
				 * System.out.println("id:" + holder.id + " pid:" + holder.pid +
				 * " " + parent);
				 */
			}

			IfcObject child = idToObject.get(holder.id);
			child.setParent(parent);

		}

		if (root == null) {
			System.out
					.println("Error in Generating & processing hierarchy for IFC Model");
		}
	}

	public void printIfcHierarchy() throws Exception {
		if (root != null) {
			try {
				writer = new PrintWriter("sample1.json");
				printHierarchy(root, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				writer.close();
				writer = null;
			}
		}
	}

	private void printHierarchy(IfcObject object, int indent) {
		writer.print("{");
		writer.print(object);
		if (object.getChildren().size() > 0) {
			writer.print(",\"ch\":[");
			List<IfcObject> list = object.getChildren();
			int len = list.size();
			for (int i = 0; i < len - 1; i++) {
				printHierarchy(list.get(i), indent + 1);
				writer.print(",");
			}
			printHierarchy(list.get(len - 1), indent + 1);
			writer.print("]");
		}
		writer.print("}");
	}

	public void release() {
		library.Cleanup();
		idToObject = null;
		idToParent = null;
		writer = null;
	}

	private static class ParentChildHolder {
		private final int id;
		private final int pid;
		private int fhashcode = 0;

		public ParentChildHolder(int id, int pid) {
			super();
			this.id = id;
			this.pid = pid;
		}

		@Override
		public int hashCode() {
			if (fhashcode == 0) {
				int result = HashCodeUtil.SEED;
				result = HashCodeUtil.hash(result, id);
				result = HashCodeUtil.hash(result, pid);
				fhashcode = result;
			}
			return fhashcode;
		}

		@Override
		public boolean equals(Object that) {
			if (this == that)
				return true;

			if (!(that instanceof ParentChildHolder))
				return false;

			ParentChildHolder athat = (ParentChildHolder) that;

			return this.id == athat.id && this.pid == athat.pid;
		}

	}

	public static void main(String[] args) throws Exception {
		ProcessIfcFile ifcFile = new ProcessIfcFile("lib/libifcjni2.so");
		long t = System.currentTimeMillis();
		System.out.println("Parsing File...");
		ifcFile.parse("files/IFC.ifc");
		System.out.println("Parsing File Done (Time taken: "
				+ (System.currentTimeMillis() - t) / (60000.0) + " ) mins");
		System.out.println("Creating Geometry...");
		ifcFile.parseGeometry();
		System.out.println("Creating Geometry Done (Time taken: "
				+ (System.currentTimeMillis() - t) / (60000.0) + " ) mins");
		System.out.println("Processing Hierarchy");
		ifcFile.processHierarchy();
		System.out.println("Processing Hierarchy Done (Time taken: "
				+ (System.currentTimeMillis() - t) / (60000.0) + " ) mins");
		ifcFile.printIfcHierarchy();
		ifcFile.release();
		System.out.println("Releasing Resource Done (Time taken: "
				+ (System.currentTimeMillis() - t) / (60000.0) + " ) mins");
	}

}
