import java.io.IOException;
import java.nio.file.Path;

/**
 * This class is to build a InvertedIndex as threaded version.
 * 
 * @author alex
 *
 */
public class ThreadedInvertedIndexBuilder extends InvertedIndexBuilder {

	/**
	 * The Inverted Index to populate.
	 */
	private final ThreadedInvertedIndex invertedIndex;
	
	/**
	 * The number or threads.
	 */
	private final int numThreads;

	/**
	 * Constructof for the ThreadedInvertedIndexBuilder
	 * @param invertedIndex
	 * @param numThreads 
	 */
	public ThreadedInvertedIndexBuilder(ThreadedInvertedIndex invertedIndex, int numThreads) {
		super(invertedIndex);
		this.invertedIndex = invertedIndex;
		this.numThreads = numThreads;
	}
	
	@Override
	public void traversePath(Path path) throws IOException {
		WorkQueue queue = new WorkQueue(numThreads);
		for (Path currentPath : getTextFiles(path)) {
			if(isTextFile(currentPath)) {
				queue.execute(new Task(currentPath));
			}
		}
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
		queue.shutdown();
	}

	/**
	 * This is the inner runnable task class
	 * @author alex
	 *
	 */
	private class Task implements Runnable {

		/**
		 * Path to the file in question.
		 */
		private final Path path;
		
		/**
		 * Constructor for our class Task
		 * @param path we passing
		 */
		public Task(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			try {
				 InvertedIndex local = new InvertedIndex();
				 addPath(path, local);
				 invertedIndex.addAll(local);
				 
			} catch (IOException e) {
				System.out.println("Problem encountered while adding file: " + path.toString());
			}
		}
	}
}
