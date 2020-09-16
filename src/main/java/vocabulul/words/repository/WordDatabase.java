package vocabulul.words.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;

import vocabulul.words.data.Translation;

@Repository
public class WordDatabase {

	private final List<Translation> words;
	private final List<Translation> unknownWords;

	public WordDatabase() {
		this.words = new ArrayList<>();
		this.unknownWords = new ArrayList<>();
	}

	public void addTranslationPair(Translation wordPair) {
		Preconditions.checkArgument(wordPair.isTranslated(), "Cannot add a word pair without a translation");
		words.add(wordPair);
	}

	public void addUnknownWord(Translation unknownWord) {
		Preconditions.checkArgument(!unknownWord.isTranslated(), "Cannot add an unknown word with a translation");
		unknownWords.add(unknownWord);
	}

	public List<Translation> getWords() {
		return words;
	}

	public List<Translation> getUnknownWords() {
		return unknownWords;
	}
}
