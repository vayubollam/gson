package suncor.com.android.ui.home.cards;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import suncor.com.android.R;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class CardItem {
    @StringRes
    private int title;
    @StringRes
    private int balanceTemplate;
    @ColorInt
    private int backgroundColor;
    @ColorInt
    private int textColor = Color.WHITE;
    private String balanceValue;
    private CardType cardType;
    private CardDetail.CardCategory cardCategory;
    private CardDetail cardDetail;

    public CardItem(CardDetail cardDetail) {
        this.cardDetail = cardDetail;
        this.cardType = cardDetail.getCardType();
        this.cardCategory = cardDetail.getCardCategory();
        if (cardDetail.getCardCategory() == CardDetail.CardCategory.PARTNER) {
            backgroundColor = Color.WHITE;
            textColor = Color.parseColor("#CC000000");
            balanceTemplate = R.string.cards_partners_balance_template;
            balanceValue = "20%";
            switch (cardType) {
                case HBC:
                    title = R.string.cards_hbc_label;
                    break;
                case CAA:
                    title = R.string.cards_caa_label;
                    break;
                case BCAA:
                    title = R.string.cards_bcaa_label;
                    break;
                case RBC:
                    title = R.string.cards_rbc_label;
                    break;
                case MORE:
                    title = R.string.cards_more_label;
                    balanceTemplate = R.string.cards_partners_more_balance;
                    balanceValue = "";
                    break;
            }
        } else {
            switch (cardType) {
                case FSR:
                    backgroundColor = Color.parseColor(cardDetail.getCpl() == 0.10f ? "#FF6D6E6F" : "#FFAB252C");
                    title = R.string.cards_fsr_label;
                    balanceTemplate = cardDetail.getBalance() == 1 ? R.string.cards_fsr_balance_template_singular : R.string.cards_fsr_balance_template_plural;
                    balanceValue = cardDetail.getBalance() != -1 ? CardFormatUtils.formatBalance(cardDetail.getBalance()) : null;
                    break;
                case SP:
                    backgroundColor = Color.parseColor("#FF232C32");
                    title = R.string.cards_sp_label;
                    balanceTemplate = cardDetail.getBalance() == 1 ? R.string.cards_sp_balance_template_singular : R.string.cards_sp_balance_template_plural;
                    balanceValue = cardDetail.getBalance() != -1 ? CardFormatUtils.formatBalance(cardDetail.getBalance()) : null;
                    break;
                case WAG:
                    backgroundColor = Color.parseColor("#FF143557");
                    title = R.string.cards_wag_label;
                    balanceTemplate = cardDetail.getBalance() == 1 ? R.string.cards_wag_balance_template_singular : R.string.cards_wag_balance_template_plural;
                    balanceValue = cardDetail.getBalance() != -1 ? CardFormatUtils.formatBalance(cardDetail.getBalance()) : null;

                    break;
                case PPC:
                    backgroundColor = Color.parseColor("#FFE4E4E5");
                    title = R.string.cards_ppc_label;
                    balanceTemplate = cardDetail.getBalance() == 1 ? R.string.cards_ppc_balance_template_singular : R.string.cards_ppc_balance_template_plural;
                    balanceValue = cardDetail.getBalance() != -1 ? CardFormatUtils.formatBalance(cardDetail.getBalance()) : null;
                    textColor = Color.BLACK;
                    break;
            }
        }
    }

    public CardDetail.CardCategory getCardCategory() {
        return cardCategory;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getBalanceTemplate() {
        return balanceTemplate;
    }

    public String getBalanceValue() {
        return balanceValue;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CardDetail getCardDetail() {
        return cardDetail;
    }

    public int getTitle() {
        return title;
    }

}
