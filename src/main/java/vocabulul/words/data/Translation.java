package vocabulul.words.data;

import java.util.Comparator;
import java.util.List;

public class Translation implements Comparable<Translation> {
	private static final Comparator<Translation> COMPARATOR = Comparator
		.comparing(Translation::isTranslated)
		.reversed()
		.thenComparing(Translation::getBaseWord);

	private final String baseWord;
	private final List<String> translations;

	public Translation(String baseWord, List<String> translations) {
		this.baseWord = baseWord;
		this.translations = translations;
	}

	public String getBaseWord() {
		return baseWord;
	}

	public List<String> getTranslations() {
		return translations;
	}

	@Override
	public int compareTo(Translation o) {
		return COMPARATOR.compare(this, o);
	}

	public boolean isTranslated() {
		return baseWord != null && translations != null && !translations.isEmpty();
	}
}
