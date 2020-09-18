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

import com.google.common.collect.ImmutableList;

import vocabulul.words.data.SingleTranslation;

@Component
public class WordTranslator {

	/**
	 * English-to-German base URL of dict.cc
	 */
	private static final String DICT_URL_EN_TO_DE = "https://en-de.dict.cc/?s=";

	/**
	 * The maximum translations to fetch
	 */
	private static final int LIMIT = 30;
	
	/**
	 * Attempts to translate this word using dict.cc
	 * 
	 * @param inputWord the word to translate
	 * @return a List containing up to 12 translations for the given word, empty if
	 *         the translation failed
	 */
	public List<SingleTranslation> translate(String inputWord) {
		try {
			Optional<String> normalizedWord = WordUtil.normalizeWord(inputWord);
			if (normalizedWord.isPresent()) {
				List<SingleTranslation> commonTranslations = getDictccTranslations(normalizedWord.get());
				if (!commonTranslations.isEmpty()) {
					return commonTranslations;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImmutableList.of();
	}

	/**
	 * Get translations using dict.cc. This method fetches HTML using Jsoup and
	 * gradually goes through the translations from the top. All the results are
	 * fetched in the same order they appear in on dict.cc.
	 * 
	 * @param inputWord the word to translate
	 * @return the list of translations, maximum 30
	 * @throws IOException if the website couldn't be reached or parsed, or if the
	 *                     input word was malformed
	 */
	private List<SingleTranslation> getDictccTranslations(String inputWord) throws IOException {
		List<SingleTranslation> translations = new ArrayList<>();
		String url = DICT_URL_EN_TO_DE + URLEncoder.encode(inputWord, StandardCharsets.UTF_8.toString()).toString();
		Document document = Jsoup.connect(url).get();
		Elements rows = document.select("#maincontent > table tr");
		for (Element row : rows) {
			if (Objects.equals(row.id(), "tr" + (translations.size() + 1))) {
				Elements rowTds = row.select("td.td7nl");
				String baseExpression = findWord(rowTds.get(0));
				String translatedExpression = findWord(rowTds.get(1));
				if (baseExpression != null && translatedExpression != null) {
					translations.add(SingleTranslation.of(baseExpression, translatedExpression));
					if (translations.size() >= LIMIT) {
						break;
					}
				}
			}
		}
		return translations;
	}

	private String findWord(Element element) {
		element.select("div").remove();
		return element.wholeText().replaceAll("\\{.*\\}", "").replaceAll("\\s+", " ").strip();
	}
}
