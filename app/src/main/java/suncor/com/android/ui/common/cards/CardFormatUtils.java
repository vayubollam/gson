package suncor.com.android.ui.common.cards;

import android.text.Editable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CardFormatUtils {

    public final static String CARDS_PREFIX = "7069";

    private final static Pattern PPTS_MATCHER = Pattern.compile("^" + CARDS_PREFIX + "$");

    private final static Pattern FSR_MATCHER = Pattern.compile("^" + CARDS_PREFIX + "885\\d*$");

    private final static Pattern WAG_SP_MATCHER = Pattern.compile("^" + CARDS_PREFIX + "8100\\d*$");

    private final static Pattern PPC_MATCHER = Pattern.compile("^" + CARDS_PREFIX + "(997(00|900|95|10|20|005|986|5|7)|89(20|21|31))\\d*$");

    private final static Pattern FSR_SHORT_MATCHER = Pattern.compile("^885\\d*$");

    private final static Pattern WAG_SP_SHORT_MATCHER = Pattern.compile("^8100\\d*$");

    private final static Pattern PPC_SHORT_MATCHER = Pattern.compile("^(997(00|900|95|10|20|005|986|5|7)|89(20|21|31))\\d*$");

    private final static Pattern PARTNER_MATCHER = Pattern.compile("^\\d+$");


    public final static CardFormat PPTS_FORMAT = new CardFormat(PPTS_MATCHER, new int[]{4, 5, 4, 3}, 16);
    public final static CardFormat FSR_FORMAT = new CardFormat(FSR_MATCHER, new int[]{4, 3, 3, 3, 1, 4}, 18);
    public final static CardFormat WAG_SP_FORMAT = new CardFormat(WAG_SP_MATCHER, new int[]{4, 5, 5, 4}, 18);
    public static final CardFormat PPC_FORMAT = new CardFormat(PPC_MATCHER, new int[]{4, 3, 3, 3, 1, 4}, 18);

    public final static CardFormat FSR_SHORT_FORMAT = new CardFormat(FSR_SHORT_MATCHER, new int[]{3, 3, 3, 1, 4}, 14);
    public final static CardFormat WAG_SP_SHORT_FORMAT = new CardFormat(WAG_SP_SHORT_MATCHER, new int[]{5, 5, 4}, 14);
    public static final CardFormat PPC_SHORT_FORMAT = new CardFormat(PPC_SHORT_MATCHER, new int[]{3, 3, 3, 1, 4}, 14);

    public static final CardFormat PARTNER_CARD_FORMAT = new CardFormat(PARTNER_MATCHER, new int[]{4, 5, 4, 4}, 18);
    public static final CardFormat HBC_CARD_FORMAT = new CardFormat(PARTNER_MATCHER, new int[]{6, 3, 3, 3}, 15);


    private final static CardFormat[] PETRO_CANADA_CARDS = new CardFormat[]{FSR_FORMAT, FSR_SHORT_FORMAT, WAG_SP_FORMAT, WAG_SP_SHORT_FORMAT, PPC_FORMAT, PPC_SHORT_FORMAT};


    public static String formatForViewing(CharSequence cardNumber) {
        //remove previous spaces
        CardFormat cardFormat = findCardFormat(cardNumber);
        return formatForViewing(cardNumber, cardFormat);
    }

    public static String formatForViewing(CharSequence cardNumber, CardFormat cardFormat) {
        String cleanedCardNumber = clean(cardNumber);

        if (cardFormat == null) {
            return cleanedCardNumber;
        }

        int[] format = cardFormat.getFormat();

        StringBuilder builder = new StringBuilder();

        int length = cleanedCardNumber.length();
        int start = 0;
        int end = 0;
        for (int p : format) {
            end += p;
            final boolean isAtEnd = (end >= length);
            builder.append(cleanedCardNumber.substring(start, isAtEnd ? length : end));
            if (!isAtEnd) {
                builder.append(" ");
                start += p;
            } else {
                break;
            }
        }

        return builder.toString();
    }

    public static void formatForViewing(Editable cardNumber, CardFormat cardFormat) {
        if (cardFormat == null) {
            return;
        }

        clean(cardNumber);

        int[] format = Arrays.copyOf(cardFormat.getFormat(), cardFormat.getFormat().length);


        for (int i = 1; i < format.length; i++) {
            format[i] += format[i - 1];
        }

        for (int i = format.length - 1; i >= 0; i--) {
            if (cardNumber.length() > format[i]) {
                cardNumber.insert(format[i], " ");
            }
        }
    }

    public static CardFormat findCardFormat(CharSequence cardNumber) {
        String cleanedNumber = clean(cardNumber);
        for (CardFormat format : PETRO_CANADA_CARDS) {
            if (format.getMatcherPattern().matcher(cleanedNumber).matches()) {
                return format;
            }
        }
        return null;
    }

    public static String formatBalance(int balance) {
        NumberFormat numberFormat =  DecimalFormat.getInstance();
        numberFormat.setGroupingUsed(true);

        return numberFormat.format(balance);
    }

    private static String clean(CharSequence cardNumber) {
        return cardNumber.toString().replace(" ", "");
    }

    private static void clean(Editable s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                s.replace(i, i + 1, "");
            }
        }
    }
}
