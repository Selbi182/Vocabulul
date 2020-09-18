package vocabulul.words.repository;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import vocabulul.words.data.TranslationMapping;

@Repository
public class WordDatabase {

	private static final Logger LOGGER = Logger.getLogger(WordDatabase.class.getName());

	private final Set<TranslationMapping> words;
	private final Set<TranslationMapping> unknownWords;

	public WordDatabase() {
		this.words = new HashSet<>();
		this.unknownWords = new HashSet<>();
	}

	/**
	 * Add a new word translation mapping to the database. If the list of
	 * translations is empty, this entry is marked as "unknown". If the base word is
	 * already known, nothing will be done. If the base word was already memorized
	 * as unknown but the current callee provided translations, it will be set to a
	 * known word.
	 * 
	 * @param wordPair the word to add
	 * @return true if the word was added, false if was already in the database
	 */
	public boolean storeWord(TranslationMapping wordPair) {
		if (wordPair.isTranslated()) {
			if (unknownWords.contains(wordPair)) {
				unknownWords.remove(wordPair);
			}
			boolean wasAdded = words.add(wordPair);
			if (wasAdded) {
				LOGGER.info("New Translation: " + wordPair);				
			} else {
				LOGGER.info("Word already exists: " + wordPair);
			}
			return wasAdded;
		} else {
			if (!words.contains(wordPair) && !unknownWords.contains(wordPair)) {
				LOGGER.info("New UNKNOWN Translation: " + wordPair);
				return unknownWords.add(wordPair);
			}
		}
		return false;
	}

	public Set<TranslationMapping> getWords() {
		return words;
	}

	public Set<TranslationMapping> getUnknownWords() {
		return unknownWords;
	}
}
