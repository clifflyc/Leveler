
import java.io.*;

public class FileIO {
	public static int[][] read() {

		// The name of the file to open.
		String fileName = "G:\\temp.txt";

		// This will reference one line at a time
		String line = null;

		int[][] grid = null;

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

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
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (NumberFormatException e){
			System.out.println("Error converting character to number in file '" + fileName + "'");	
		} catch (Exception ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
		return grid;
	}

	public static void write(int[][] grid) {
		int size = grid.length;
		// The path and name of the file to write to.
		String fileName = "G:\\temp.txt";

		try {
			// Assume default encoding.
			FileWriter fileWriter = new FileWriter(fileName);

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
		} catch (IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");

		}
	}
}
