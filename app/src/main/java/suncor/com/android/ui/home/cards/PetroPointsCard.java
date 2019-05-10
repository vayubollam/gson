package suncor.com.android.ui.home.cards;

import java.text.NumberFormat;

import androidx.annotation.StringRes;
import suncor.com.android.R;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;

public class PetroPointsCard {
    @StringRes
    private int balanceTemplate;
    private String balanceValue;
    @StringRes
    private int monetaryBalanceTemplate;
    private String monetaryBalanceValue;

    public PetroPointsCard(CardDetail cardDetail) {
        if (cardDetail.getCardType() != CardType.PPTS) {
            throw new IllegalArgumentException("this constructor is only PPTS for cards");
        }
        balanceTemplate = R.string.cards_ppts_balance_template;
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
        balanceValue = numberFormat.format(cardDetail.getBalance());
        monetaryBalanceTemplate = R.string.cards_ppts_monetary_balance_template;
        monetaryBalanceValue = String.valueOf(cardDetail.getBalance() / 1000);
    }

    public int getBalanceTemplate() {
        return balanceTemplate;
    }

    public String getBalanceValue() {
        return balanceValue;
    }

    public int getMonetaryBalanceTemplate() {
        return monetaryBalanceTemplate;
    }

    public String getMonetaryBalanceValue() {
        return monetaryBalanceValue;
    }
}