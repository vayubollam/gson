package suncor.com.android.ui.home.cards.list;

import android.content.Context;

import suncor.com.android.R;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class PetroPointsCard {
    private String balance;
    private String monetaryBalance;

    public PetroPointsCard(Context context, CardDetail cardDetail) {
        if (cardDetail.getCardType() != CardType.PPTS) {
            throw new IllegalArgumentException("this constructor is only PPTS for cards");
        }
        balance = context.getString(R.string.cards_ppts_balance_template, CardFormatUtils.formatBalance(cardDetail.getBalance()));
        monetaryBalance = context.getString(R.string.cards_ppts_monetary_balance_template, CardFormatUtils.formatBalance(cardDetail.getBalance() / 1000));
    }

    public String getMonetaryBalance() {
        return monetaryBalance;
    }

    public String getBalance() {
        return balance;
    }
}
