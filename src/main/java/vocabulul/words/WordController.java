package vocabulul.words;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;

import vocabulul.words.data.SingleTranslation;
import vocabulul.words.data.TranslationMapping;
import vocabulul.words.repository.WordDatabase;
import vocabulul.words.translate.WordTranslator;

@RestController
public class WordController {
	private WordTranslator wordTranslator;
	private WordDatabase wordDatabase;

	public WordController(WordTranslator wordTranslator, WordDatabase wordDatabase) {
		this.wordTranslator = wordTranslator;
		this.wordDatabase = wordDatabase;
	}

	/**
	 * @deprecated Only for testing purposes!
	 */
	@Deprecated
	@EventListener(ApplicationReadyEvent.class)
	private void populateTestData() {
		addNewWords(ImmutableList.of("repentance", "inoculum", "mirage", "verity"));
		addNewWords(ImmutableList.of("dsaifubnasdf", "dfgdsafga", "prollydoesntexist"));
	}

	@RequestMapping("/translate/{word}")
	public ResponseEntity<TranslationMapping> justTranslate(@PathVariable String word, @RequestParam(required = false) Integer limit) {
		List<SingleTranslation> translations = wordTranslator.translate(word);
		if (limit != null) {
			translations = translations.stream().limit(Math.max(0, limit)).collect(Collectors.toList());
		}
		TranslationMapping of = TranslationMapping.of(word, translations);
		return ResponseEntity.ok(of);
	}

	@RequestMapping("/add/{word}")
	public ResponseEntity<List<TranslationMapping>> addSingleWord(@PathVariable String word) {
		return addNewWords(ImmutableList.of(word));
	}

	@RequestMapping("/add")
	public ResponseEntity<List<TranslationMapping>> addNewWords(@RequestBody Collection<String> words) {
		List<TranslationMapping> responseBody = new ArrayList<>();
		for (String word : words) {
			List<SingleTranslation> translations = wordTranslator.translate(word);
			TranslationMapping translation = TranslationMapping.of(word, translations);
			wordDatabase.storeWord(translation);
			responseBody.add(translation);
		}

		Collections.sort(responseBody, Comparator.comparing(TranslationMapping::isTranslated).reversed());

		return ResponseEntity
			.status(responseBody.isEmpty() ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED)
			.body(responseBody);
	}

	@GetMapping("/words")
	public ResponseEntity<Set<TranslationMapping>> getAllTranslatedWordPairs() {
		Set<TranslationMapping> words = wordDatabase.getWords();
		return ResponseEntity.ok(words);
	}

	@GetMapping("/unknown")
	public ResponseEntity<Set<TranslationMapping>> getAllUnknownWords() {
		Set<TranslationMapping> words = wordDatabase.getUnknownWords();
		return ResponseEntity.ok(words);
	}
}
