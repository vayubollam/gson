package suncor.com.android.ui.common.cards;

import java.util.regex.Pattern;

public class CardFormat {
    private Pattern matcherPattern;
    private int[] format;
    private int length;

    public CardFormat(Pattern matcherPattern, int[] format, int length) {
        this.matcherPattern = matcherPattern;
        this.format = format;
        this.length = length;
    }

    public Pattern getMatcherPattern() {
        return matcherPattern;
    }

    public int[] getFormat() {
        return format;
    }

    /**
     * @return the required length without any spaces
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the length after formatting the card number
     */
    public int getFormattedLength() {
        return length + format.length - 1;
    }
}
