package vocabulul.words;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vocabulul.words.data.Translation;
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

	@PostMapping("/add")
	public ResponseEntity<List<Translation>> addNewWords(@RequestBody Collection<String> words) {
		List<Translation> responseBody = new ArrayList<>();
		for (String word : words) {
			wordTranslator.translate(word)
				.ifPresentOrElse(translations -> {
					Translation wordPair = new Translation(word, translations);
					wordDatabase.addTranslationPair(wordPair);
					responseBody.add(wordPair);
				}, () -> {
					Translation unknownWord = new Translation(word, null);
					wordDatabase.addUnknownWord(unknownWord);
					responseBody.add(unknownWord);
				});
		}

		Collections.sort(responseBody, Comparator.comparing(Translation::isTranslated).reversed());
		
		return ResponseEntity
			.status(responseBody.isEmpty() ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED)
			.body(responseBody);
	}
	
	@GetMapping("/words")
	public ResponseEntity<List<Translation>> getAllTranslatedWordPairs() {
		List<Translation> words = wordDatabase.getWords();
		return ResponseEntity.ok(words);
	}
	
	@GetMapping("/unknown")
	public ResponseEntity<List<Translation>> getAllUnknownWords() {
		List<Translation> words = wordDatabase.getUnknownWords();
		return ResponseEntity.ok(words);
	}
}
