package Databases;

/**
 * This class contains a set of methods to work with tables.
 * @author Nikodim
 *
 */
public class Tables {

	private String textJustification(String b, int LIM) {
		String bTemp;
		b = ((b.length() > LIM) ? b.substring(0, LIM - 1) : b);
		bTemp = "";
		for (int z = 0; z < LIM - b.length(); z++) {
			bTemp += " ";
		}
		b += bTemp;
		return b;
	}

	/**
	 * This prints a table based on the data, having been taken from an array in the particular format.
	 * @param buffer
	 */
	public void printTable(String[] buffer, int col_size) {
		final int defaultSize = 8;
		int x, y, c, l;
		String b;
		col_size = ((col_size < 1) ? defaultSize : col_size);
		c = Integer.parseInt(buffer[0]);
		l = ((buffer.length - c - 1 < 0) ? 0 : buffer.length - c - 1);
		// Building a table
		System.out.println();
		for (x = 0; x < c; x++) {
			System.out.print(this.textJustification(buffer[x + 1], col_size) + "\t|");
		}
		System.out.println();
		for (x = 0; x < (int) (l / c); x++) {
			for (y = 0; y < c; y++) {
				b = buffer[(c * x + c + 1) + y];
				System.out.print(this.textJustification(b, col_size) + "\t|");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Total number of lines: " + String.valueOf(l) + "\n");
	}

}