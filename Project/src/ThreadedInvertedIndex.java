import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This is Thread-safe version of InvertedIndex using read-write lock.
 *  
 * @author alex
 */
public class ThreadedInvertedIndex extends InvertedIndex {
	/**
	 * This is lock object that will be used for multithreading.
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * This function initialized the Thread-safe Inverted Index;
	 */
	public ThreadedInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}
	
	@Override
	public void addAll(InvertedIndex other) {
		lock.writeLock().lock();
		try {
			super.addAll(other);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(String path, String[] element) {
		lock.writeLock().lock();
		try {
			super.addAll(path, element);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(String path, String[] element, int start) {
		lock.writeLock().lock();
		try {
			super.addAll(path, element, start);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean addEntry(String word, String filename, int position) {
		lock.writeLock().lock();
		try {
			return super.addEntry(word, filename, position);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public Map<String, Integer> getCounts() {
		lock.readLock().lock();
		try {
			return super.getCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void printIndex(Path outputFile) throws IOException {
		lock.readLock().lock();
		try {
			super.printIndex(outputFile);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public boolean contains(String word, String path, int index) {
		lock.readLock().lock();
		try {
			return super.contains(word, path, index);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int size(String word) {
		lock.readLock().lock();
		try {
			return super.size(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<SearchResult> exactSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<SearchResult> partialSearch(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}
}
