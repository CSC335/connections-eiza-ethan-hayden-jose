import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GameDataCollection {
	private final static String FILE_GAME_MARKER = "game";
	private final static String WORD_SEPARATOR = ",";
	private final static String HINT_SEPARATOR = "@";
	private final static int WORDS_PER_COLOR = 4;
	private final static int COLOR_COUNT = 4;
	private List<GameData> gameList;

	/**
	 * Initializes the GameDataCollection with a String file path to read the games
	 * from.
	 * 
	 * @param filePath String path to read games from
	 */
	public GameDataCollection(String filePath) {
		gameList = new ArrayList<>();
		readFile(filePath);
	}

	/**
	 * Enhances functionality of Scanner to include line numbers.
	 */
	private class FileReader {
		private int lineNumber;
		private String path;
		private Scanner scanner;

		/**
		 * Initializes FileReader with a String file path to read from, which is passed
		 * into a Scanner.
		 * 
		 * @param filePath String path to read from
		 * @throws FileNotFoundException if the file could not be found
		 */
		public FileReader(String filePath) throws FileNotFoundException {
			scanner = new Scanner(new File(filePath));
			path = filePath;
		}

		/**
		 * Returns true if another line is available to read and false if otherwise.
		 * 
		 * @return true if another line can be read
		 */
		public boolean hasNextLine() {
			return scanner.hasNextLine();
		}

		/**
		 * Returns the String of the next line if it is available, otherwise throws a
		 * NoSuchElementException.
		 * 
		 * @return the String of the next line
		 * @throws NoSuchElementException if the next line is not available
		 */
		public String nextLine() throws NoSuchElementException {
			if (scanner.hasNextLine()) {
				lineNumber++;
			}
			return scanner.nextLine();
		}

		/**
		 * Returns the number of line the FileReader is currently on (or has last read
		 * from).
		 * 
		 * @return the int current line number
		 */
		public int getLineNumber() {
			return lineNumber;
		}

		/**
		 * Returns the file path that the FileReader was initialized with.
		 * 
		 * @return the String file path
		 */
		public String getPath() {
			return path;
		}
	}

	/**
	 * Returns true if the provided file to a text file of NYT Connections games
	 * could be successfully read and false if otherwise. The file contains a
	 * sequence of games and no whitespace or blank lines separating them or their
	 * parameters. Each game is began with the keyword "game" followed by a sequence
	 * of the four colors (yellow, green, blue, purple), each with the following
	 * parameters in this order: description, hint list, and word list.
	 * 
	 * @param filePath the String path to read from
	 * @return true if the file was successfully read and false if otherwise
	 */
	@SuppressWarnings("hiding")
	public boolean readFile(String filePath) {
		try {
			List<GameData> fileGameList = new ArrayList<>();
			FileReader fileReader = new FileReader(filePath);

			while (fileReader.hasNextLine()) {
				String line = fileReader.nextLine();

				if (!line.contentEquals(FILE_GAME_MARKER)) {
					readError(fileReader, String.format("expected game marker \"%s\" but read \"%s\" instead",
							FILE_GAME_MARKER, line));
					return false;
				}

				Map<DifficultyColor, GameAnswerColor> answerMap = new HashMap<>();

				for (int i = 0; i < COLOR_COUNT; i++) {
					try {
						String colorString = fileReader.nextLine();
						DifficultyColor color;
						try {
							color = DifficultyColor.valueOf(colorString.toUpperCase());
						} catch (IllegalArgumentException e) {
							readError(fileReader, String.format("\"%s\" is not a valid color", colorString));
							return false;
						}

						String label = fileReader.nextLine();
						if (removeWhiteSpace(label).isEmpty()) {
							readError(fileReader, "the description is empty");
							return false;
						}

						String hintListString = fileReader.nextLine();
						String[] hintList = hintListString.split(HINT_SEPARATOR);
						for (String hint : hintList) {
							if (removeWhiteSpace(hint).isEmpty()) {
								readError(fileReader, "there is an empty hint in the hint list");
								return false;
							}
						}

						String wordListString = fileReader.nextLine();
						String[] wordList = wordListString.split(WORD_SEPARATOR);
						if (wordList.length != WORDS_PER_COLOR) {
							readError(fileReader,
									String.format("the the list of words is not of length %d", WORDS_PER_COLOR));
							return false;
						}
						for (String word : wordList) {
							if (removeWhiteSpace(word).isEmpty()) {
								readError(fileReader, "there is an empty word in the word list");
								return false;
							}
						}
						answerMap.put(color, new GameAnswerColor(color, label, hintList, wordList));
					} catch (NoSuchElementException e) {
						readError(fileReader,
								"not enough lines to describe either the color, description, or list of words for the game");
						return false;
					}
				}
				fileGameList.add(new GameData(answerMap));
			}
			gameList = fileGameList;
			return true;
		} catch (FileNotFoundException e) {
			readError(filePath, "file could not be found");
		} catch (IOException e) {
			readError(filePath, "an unknown issue occurred when attempting to read the file");
		}
		return false;
	}

	/**
	 * Returns the List of games as GameData objects.
	 * 
	 * @return the List of games
	 */
	public List<GameData> getGameList() {
		return gameList;
	}

	/**
	 * Helper method for printing an error reading a file with the given FileReader.
	 * 
	 * @param fileReader the FileReader that read the file in question
	 * @param content    the message of the error
	 */
	private void readError(FileReader fileReader, String content) {
		System.out.printf("Error reading game words file \"%s\" (line %d): %s\n", fileReader.getPath(),
				fileReader.getLineNumber(), content);
	}

	/**
	 * Helper method for printing an error reading a file with the given path.
	 * 
	 * @param filePath the String path of the file in question
	 * @param content  the message of the error
	 */
	private void readError(String filePath, String content) {
		System.out.printf("Error reading game words file \"%s\": %s\n", filePath, content);
	}

	/**
	 * Helper method that removes whitespace from a given String.
	 * 
	 * @param string the String to remove whitespace from
	 * @return the cleaned String without whitespace
	 */
	private String removeWhiteSpace(String string) {
		return string.replaceAll("\\s", "");
	}
}
