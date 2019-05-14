package suncor.com.android.ui.common.cards;

import java.util.regex.Pattern;

public class CardFormat {
    private Pattern matcherPattern;
    private Pattern validationPattern;
    private int[] format;

    public CardFormat(Pattern matcherPattern, Pattern validationPattern, int[] format) {
        this.matcherPattern = matcherPattern;
        this.validationPattern = validationPattern;
        this.format = format;
    }

    public Pattern getValidationPattern() {
        return validationPattern;
    }

    public Pattern getMatcherPattern() {
        return matcherPattern;
    }

    public int[] getFormat() {
        return format;
    }
}
