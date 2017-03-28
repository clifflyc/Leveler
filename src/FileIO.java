import java.io.*;

/**
 * This class contains two static methods that will read/write a 2D integer
 * array to/from a file.
 * <p>
 * Identifiers:
 * <li>static final String FILEPATH - the path of the file to read or write to.
 */
public class FileIO {
	static final String FILEPATH = "D:\\TestFile1.txt";

	/**
	 * read method: reads a grid of numbers from the file at the path specified
	 * by {@code FILEPATH}. If any errors occur, an error message will be
	 * printed to the console.
	 * <p>
	 * Local variables:
	 * <li>String line - stores the current line being read
	 * <li>int[][] grid - the array that the read numbers will be store to
	 * <li>FileReader fileReader - the fileReader used to read the file
	 * <li>BufferedReader bufferedReader - the BufferedReader used to wrap
	 * around the FileReader
	 * 
	 * @param filePath
	 *            the path for the file, if blank, reads from default path
	 * @return an 2D integer array containing the numbers read, or {@code null}
	 *         if the reading operation has failed
	 */
	public static int[][] read(String filePath) {
		// This will reference one line at a time
		String line = null;
		int[][] grid = null;

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(filePath.equals("") ? FILEPATH : filePath);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			line = bufferedReader.readLine();
			if (line != null) {
				int size = line.length();
				grid = new int[size][size];
				for (int j = 0; j < size; j++) {
					grid[0][j] = Integer.parseInt(line.charAt(j) + "");
				}
				for (int i = 1; i < size; i++) {
					line = bufferedReader.readLine();
					for (int j = 0; j < size; j++) {
						grid[i][j] = Integer.parseInt(line.charAt(j) + "");
					}
				}

			}

			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + FILEPATH + "'");
		} catch (NumberFormatException e) {
			System.out.println("Error converting character to number in file '" + FILEPATH + "'");
		} catch (Exception ex) {
			System.out.println("Error reading file '" + FILEPATH + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
		return grid;
	}// end read class

	/**
	 * Writes the numbers of a 2D integer array to a file at the path specified
	 * by {@code FILEPATH}. If any unexpected error occurs, a message will be
	 * printed to the console.
	 * <p>
	 * Local variable
	 * <li>int size - the number of rows and columns in the array.
	 * <li>FileWriter fileWriter - the fileWriter used to write to the file
	 * <li>BufferedWriter bufferedWriter - the BufferedWriter used to wrap
	 * around the FileWriter
	 * 
	 * @param filePath
	 *            the path for the file, if blank, use default path
	 * @param grid
	 *            the 2D integer array to write to the file
	 */
	public static void write(int[][] grid, String filePath) {
		int size = grid.length;

		try {
			// Assume default encoding.
			FileWriter fileWriter = new FileWriter(filePath.equals("") ? FILEPATH : filePath);

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					bufferedWriter.write(grid[i][j] + "");
				}
				bufferedWriter.newLine();
			}

			// Always close files.
			bufferedWriter.close();
		} catch (Exception ex) {
			System.out.println("Error writing to file '" + FILEPATH + "'");

		}
	}// end write class
}// end FileIO class
