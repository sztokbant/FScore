package br.net.du.fscore.persist;

public class TableColumnsUtils {
	public String getAsCommaSeparatedString(String[] columns) {
		return this.getAsCommaSeparatedString(columns, 0);
	}

	public String getAsCommaSeparatedStringWithoutFirstColumn(String[] columns) {
		return this.getAsCommaSeparatedString(columns, 1);
	}

	private String getAsCommaSeparatedString(String[] columns, int firstColumn) {
		StringBuilder sb = new StringBuilder();
		for (int i = firstColumn; i < columns.length; i++) {
			sb.append(columns[i]);
			if (i + 1 < columns.length) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}
}
