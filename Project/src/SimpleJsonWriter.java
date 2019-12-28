import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator; //import iterator function.
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 * 
 * @author Alex Kowalczuk
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {
	
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> iterator = elements.iterator();
		writer.write("[\n");
		
		if (iterator.hasNext()) {
			indent(writer, level + 1);
			writer.write(iterator.next().toString());
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			indent(writer, level + 1);
			writer.write(iterator.next().toString());
		}
		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Map.Entry<String, Integer>> map = elements.entrySet().iterator();
		writer.write("{\n");
		
		if (map.hasNext()) {
			Map.Entry<String, Integer> entry = map.next(); 
			String Key = entry.getKey();
			Integer Value = entry.getValue();
			
			indent(writer, level + 1 );
			quote(Key, writer);
			writer.write(": " + Value); 
		}
		while (map.hasNext()) {
			Map.Entry<String, Integer> entry = map.next(); 
			String Key = entry.getKey();
			Integer Value = entry.getValue();
			
			writer.write(",\n");
			
			indent(writer, level + 1 );
			quote(Key, writer);
			writer.write(": " + Value); 
		}
		writer.write("\n}\n");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a nested JSON object.
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		indent(writer, level);
		writer.write("{\n");
		
		if (iterator.hasNext()) {
			String key = iterator.next();
			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": ");
			asArray(elements.get(key), writer, level + 1);
		}
		while(iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",\n");
			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": ");
			asArray(elements.get(key), writer, level + 1);
		}
		writer.write("\n}");
	}

	/**
	 * Returns the elements as a nested JSON object.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObject(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * This function writes the invertedIndex provided with a writer.
	 * @param elements to write
	 * @param writer to use
	 * @param level to indent level
	 * @throws IOException
	 */
	public static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");
		
		if (iterator.hasNext()) {
			String key = iterator.next();
			writer.write("\n");
			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": ");
			asNestedObject(elements.get(key), writer, level);
		}	
		while(iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",\n");
			indent(writer, level + 1);
			quote(key, writer);
			writer.write(": ");
			asNestedObject(elements.get(key), writer, level);
		}
		indent("\n}",writer, level);
	}

	/**
	 * This function calls the other invertedIndex method
	 * @param elements to write
	 * @param path file path to use
	 * @throws IOException
	 */
	public static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNested(elements, writer, 0);
		}
	}
	
	/**
	 * This function overloads asQuery.
	 *
	 * @param querySet
	 * @param path
	 * @throws IOException
	 */
	public static void asQuery(Map<String, ArrayList<InvertedIndex.SearchResult>> querySet, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQuery(querySet, path, writer, 0);
		}
	}
	
	/**
	 * This function is a overloader
	 * @param querySet set query and result
	 * @param outputFile file
	 * @throws IOException
	 */
	public static void asQuery(TreeMap<String, ArrayList<InvertedIndex.SearchResult>> querySet, Path outputFile) throws IOException {
		asQuery((Map<String, ArrayList<InvertedIndex.SearchResult>>) querySet, outputFile);
	}
	
	/**
	 * This Function write the query-result to a file.
	 * @param querySet set of queries
	 * @param path ion which will be written
	 * @param writer writer to use
	 * @param level how many indentations
	 * @throws IOException
	 */
	
	public static void asQuery(Map<String, ArrayList<InvertedIndex.SearchResult>> querySet, Path path, Writer writer, int level) throws IOException {
		Map<String, ArrayList<InvertedIndex.SearchResult>> temp = new TreeMap<>();

		for (String q : querySet.keySet()) {
			ArrayList<InvertedIndex.SearchResult> innerTemp = new ArrayList<>();
			innerTemp.addAll(querySet.get(q));
			temp.put(q, innerTemp);
		}

		querySet = temp;

		writer.write("{\n");
		var iterator = querySet.keySet().iterator();

		if (iterator.hasNext()) {
			String nextQuery = iterator.next();
			indent(writer, level +1);
			writer.write("\"" + nextQuery.toString() + "\": [");
			indent(writer, level + 1);

			var innerIterator = querySet.get(nextQuery).iterator();

			if (innerIterator.hasNext()) {
				indentextra(innerIterator, writer, level);
			}

			while (innerIterator.hasNext()) {
				withoutindent(innerIterator, writer, level);

			}
			writer.write("\n");
			indent(writer, level + 1);
			writer.write("]");
			indent(writer, level);
		}

		while (iterator.hasNext()) {
			String nextQuery = iterator.next();
			writer.write(",\n");
			indent(writer, level +1);
			writer.write("\"" + nextQuery.toString() + "\": [");
			indent(writer, level + 1);

			var innerIterator = querySet.get(nextQuery).iterator();

			boolean bob = true;
			if (innerIterator.hasNext()) {
				indentextra(innerIterator, writer, level);
			}

			while (innerIterator.hasNext()) {
				withoutindent(innerIterator, writer, level);
			}

			if(!bob) {indent(writer, level +1);}
			writer.write("\n\t]");
			indent(writer, level);
		}
		writer.write("\n}");
	}

	/**
	 * This function is helper for writing as Query.
	 *
	 * @param iterator
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	private static void withoutindent(Iterator<InvertedIndex.SearchResult> iterator, Writer writer, int level) throws IOException {
		writer.write(",\n");
		indent(writer, level + 2);
		writer.write("{\n");
		indent(writer, level + 3);
		var nexto = iterator.next();
		writer.write(nexto.getWhereString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getCountString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getScoreString() + "\n");
		indent(writer, level + 2);
		writer.write("}");
		indent(writer, level);

	}

	/**
	 * This function is helper for writing as Query.
	 *
	 * @param iterator
	 * @param writer
	 * @param level
	 * @throws IOException
	 */
	private static void indentextra(Iterator<InvertedIndex.SearchResult> iterator, Writer writer, int level) throws IOException {
		writer.write("\n");
		indent(writer, level + 1);
		writer.write("\t{\n");
		indent(writer, level + 3);
		var nexto = iterator.next();
		writer.write(nexto.getWhereString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getCountString() + "\n");
		indent(writer, level + 3);
		writer.write(nexto.getScoreString() + "\n");
		indent(writer, level + 2);

		writer.write("}");
		indent(writer, level );

	}
	
		
	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}
	
	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(element.toString(), writer, times);
	}
	
	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element);
	}
	
	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
	
	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		quote(element, writer);
	}
}