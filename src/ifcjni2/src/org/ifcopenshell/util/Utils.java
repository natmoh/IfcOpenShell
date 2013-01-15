package org.ifcopenshell.util;

public final class Utils {

	public static String floatArrayToString(String tag, float[] arr) {
		StringBuilder builder = new StringBuilder();
		if (tag != null) {
			builder.append("\"" + tag + "\"" + ":");
		}
		builder.append("[");
		for (int i = 0, len = arr.length; i < len - 1; i++)
			builder.append(arr[i] + ",");
		if (arr.length > 0)
			builder.append(arr[arr.length - 1]);
		builder.append("]");
		return builder.toString();
	}

	public static String intArrayToString(String tag, int[] arr) {
		StringBuilder builder = new StringBuilder("\"");
		builder.append(tag + "\"" + ":[");
		for (int i = 0, len = arr.length; i < len - 1; i++)
			builder.append(arr[i] + ",");
		builder.append(arr[arr.length - 1] + "]");
		return builder.toString();
	}

}
