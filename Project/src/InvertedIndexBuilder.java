import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This is Inverted Index Builder class to "connect" inverted index with a right
 * path.
 * 
 * @author alex
 *
 */
public class InvertedIndexBuilder {

	/**
	 * Inverted Index Builder
	 */
	private final InvertedIndex index;

	/**
	 * This is default SnowballStemmer algorithm.
	 */
	private static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor for the inverted index class.
	 * 
	 * @param index Inverted Index structure that will be built.
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * This function is checking if path is a text file
	 * 
	 * @param path provided to find if is a text file
	 * @return true if is a text file
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return ((lower.endsWith(".txt") || lower.endsWith(".text")) && Files.isRegularFile(path));
	}

	/**
	 * This function gets TextFiles on every paths and checks if itds text file.
	 * 
	 * @param path that we check if its text file
	 * @return list of subfiles
	 * @throws IOException
	 */
	public static List<Path> getTextFiles(Path path) throws IOException {
		return Files.walk(path, FileVisitOption.FOLLOW_LINKS).filter(InvertedIndexBuilder::isTextFile).collect(Collectors.toList());
	}

	/**
	 * This function is calling addPath function on files from directory
	 * 
	 * @param path starting path that function check.
	 * @throws IOException
	 */
	public void traversePath(Path path) throws IOException {
		for (Path thisPath : getTextFiles(path)) {
			addPath(thisPath);
		}
	}

	/**
	 * This function to add the file to inverted index strusture
	 * 
	 * @param inputFile need to be added
	 * @param index     of our inverted index
	 * @throws IOException
	 */
	public static void addPath(Path inputFile, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			String location = inputFile.toString();
			int i = 0;

			while (line != null) {
				String[] tokens = TextParser.parser(line);
				for (String words : tokens) {
					String data = stemmer.stem(words).toString();
					index.addEntry(data, location, ++i);
				}
				line = reader.readLine();
			}
		}
	}

	/**
	 * This function will add the given file to the inverted index structure.
	 * 
	 * @param inputFile we want to add
	 * @throws IOException
	 */
	public void addPath(Path inputFile) throws IOException {
		addPath(inputFile, this.index);
	}
}