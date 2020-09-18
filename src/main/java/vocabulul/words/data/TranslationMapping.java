package vocabulul.words.data;

import java.beans.Transient;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * A translation mapping holds the results of a single base word to a multiude
 * of its translations. These range from direct word-to-word translations to
 * translations that form a different meaning based on their wording or
 * expression. A mapping can also be empty if the word is bogus.
 */
public class TranslationMapping implements Comparable<TranslationMapping> {
	private static final Comparator<TranslationMapping> COMPARATOR = Comparator
		.comparing(TranslationMapping::isTranslated)
		.reversed()
		.thenComparing(TranslationMapping::getBaseWord);

	private final String baseWord;
	private final ImmutableList<SingleTranslation> translations;

	private TranslationMapping(String baseWord, ImmutableList<SingleTranslation> translations) {
		this.baseWord = baseWord;
		this.translations = translations;
	}

	public static TranslationMapping of(String baseWord, List<SingleTranslation> translations) {
		return new TranslationMapping(baseWord, ImmutableList.copyOf(translations));
	}
	
	public String getBaseWord() {
		return baseWord;
	}

	public ImmutableList<SingleTranslation> getTranslations() {
		return translations;
	}

	@Override
	public int compareTo(TranslationMapping o) {
		return COMPARATOR.compare(this, o);
	}

	@Transient
	public boolean isTranslated() {
		return baseWord != null && translations != null && !translations.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseWord == null) ? 0 : baseWord.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TranslationMapping other = (TranslationMapping) obj;
		if (baseWord == null) {
			if (other.baseWord != null)
				return false;
		} else if (!baseWord.equals(other.baseWord))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s<%d>", baseWord, translations.size());
	}
}
