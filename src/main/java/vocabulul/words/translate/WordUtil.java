package vocabulul.words.translate;

import java.util.Optional;

public final class WordUtil {

	/**
	 * Returns a normalized form of the input word. A normalized word is
	 * all-lowercase, all-latin without special characters, and only one word
	 * (because dict.cc struggles with multi-word translations).
	 * 
	 * @param word the input word to normalize
	 * @return an Optional containing the normalized word, empty if the
	 *         normalization doesn't match [a-z]+
	 */
	public static Optional<String> normalizeWord(String word) {
		String normalized = word.strip().toLowerCase();
		if (normalized.matches("[a-z]+")) {
			return Optional.of(normalized);
		}
		return Optional.empty();
	}
}
