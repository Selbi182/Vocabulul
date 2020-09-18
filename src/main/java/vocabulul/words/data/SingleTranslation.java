package vocabulul.words.data;

/**
 * Holds a singular, immutable translation. The base will usually be a single
 * word, but may also be an expression enwrapping the word and forming a new
 * meaning.
 */
public class SingleTranslation {
	private final String baseExpression;
	private final String translatedExpression;

	private SingleTranslation(String baseExpression, String translatedExpression) {
		super();
		this.baseExpression = baseExpression;
		this.translatedExpression = translatedExpression;
	}

	public String getBaseExpression() {
		return baseExpression;
	}

	public String getTranslatedExpression() {
		return translatedExpression;
	}

	public static SingleTranslation of(String baseExpression, String translatedExpression) {
		return new SingleTranslation(baseExpression, translatedExpression);
	}

	@Override
	public String toString() {
		return String.format("[%s=%s]", baseExpression, translatedExpression);
	}
}
