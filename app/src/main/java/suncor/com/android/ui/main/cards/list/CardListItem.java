package suncor.com.android.ui.main.cards.list;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import suncor.com.android.R;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class CardListItem {
    @StringRes
    private int title;
    @ColorInt
    private int backgroundColor;
    private String balance;
    @ColorInt
    private int textColor = Color.WHITE;
    private CardType cardType;
    private CardDetail.CardCategory cardCategory;
    private CardDetail cardDetail;

    public CardListItem(Context context, CardDetail cardDetail) {
        this.cardDetail = cardDetail;
        this.cardType = cardDetail.getCardType();
        this.cardCategory = cardDetail.getCardCategory();
        if (cardDetail.getCardCategory() == CardDetail.CardCategory.PARTNER) {
            backgroundColor = Color.WHITE;
            textColor = Color.parseColor("#CC000000");
            balance = context.getString(R.string.cards_partners_balance_template, context.getString(R.string.cards_partners_balance_value));
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
                    balance = context.getString(R.string.cards_partners_more_balance);
                    break;
            }
        } else {
            int balanceValue = cardDetail.getBalance();
            switch (cardType) {
                case FSR:
                    backgroundColor = Color.parseColor(cardDetail.getCpl() == 0.10f ? "#FF6D6E6F" : "#FFAB252C");
                    title = R.string.cards_fsr_label;
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_litres_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
                    break;
                case SP:
                    backgroundColor = Color.parseColor("#FF232C32");
                    title = R.string.cards_sp_label;
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_days_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
                    break;
                case WAG:
                    backgroundColor = Color.parseColor("#FF143557");
                    title = R.string.cards_wag_label;
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_washes_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
                    break;
                case PPC:
                    backgroundColor = Color.parseColor("#FFE4E4E5");
                    title = R.string.cards_ppc_label;
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_litres_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
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

    public String getBalance() {
        return balance;
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
