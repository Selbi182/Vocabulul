package vocabulul.words.translate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class WordTranslator {

	/**
	 * English-to-German base URL of dict.cc
	 */
	private static final String DICT_URL_EN_TO_DE = "https://en-de.dict.cc/?s=";
	
	/**
	 * Return up to 12 results, anything above that is just silly
	 */
	private static final int MAX_TRANSLATIONS = 12;

	/**
	 * Attempts to translate this word using dict.cc
	 * 
	 * @param inputWord the word to translate
	 * @return an Optional List containing up to 12 translations for the given word,
	 *         empty if the translation failed
	 */
	public Optional<List<String>> translate(String inputWord) {
		try {
			List<String> commonTranslations = getDictccTranslations(inputWord, MAX_TRANSLATIONS);
			if (commonTranslations.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(commonTranslations);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	/**
	 * Get translations using dict.cc. This method fetches HTML using Jsoup and
	 * gradually goes through the translations from the top. It is assumed that only
	 * direct word-to-word translations are wanted, so the procedure stops at the
	 * first separator line (e.g. "test" vs. "to test").
	 * 
	 * @param inputWord the word to translate
	 * @param limit     how many translations should be returned; disabled = 0
	 * @return the list of translations
	 * @throws IOException if the website couldn't be reached or parsed, or if the
	 *                     input word was malformed
	 */
	private List<String> getDictccTranslations(String inputWord, int limit) throws IOException {
		List<String> translations = new ArrayList<>();
		String url = DICT_URL_EN_TO_DE + URLEncoder.encode(inputWord, StandardCharsets.UTF_8.toString()).toString();
		Document document = Jsoup.connect(url).get();
		Elements rows = document.select("#maincontent > table tr");
		boolean foundFirst = false;
		for (Element row : rows) {
			if (Objects.equals(row.id(), "tr" + (translations.size() + 1))) {
				foundFirst = true;
				Element rightTd = row.select("td.td7nl").get(1);
				Element word = null;
				for (Element a : rightTd.select("a")) {
					if (a.childrenSize() == 0) {
						word = a;
						break;
					}
				}
				if (word == null) {
					continue;
				}
				translations.add(word.html().strip());
				if (limit > 0 && translations.size() >= limit) {
					break;
				}
			} else if (foundFirst) {
				break;
			}
		}
		return translations;
	}
}
