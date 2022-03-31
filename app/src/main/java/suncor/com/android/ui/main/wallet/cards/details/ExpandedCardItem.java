package suncor.com.android.ui.main.wallet.cards.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import suncor.com.android.R;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class ExpandedCardItem {
    private String cardName;
    private Drawable cardImage;
    private String balance;
    private String balanceDetails;
    private boolean isBalanceDetailsVisible = true;
    private String cardDescription;
    private String cardNumber;
    private CardType cardType;
    private Drawable barCode;
    private CardDetail.CardCategory cardCategory;
    private CardDetail cardDetail;
    private boolean isRemovable = true;
    private int width = 0;
    private int height = 0;
    private int vacuumRemaining;
    private int balanceRemaining;

    public ExpandedCardItem(Context context, CardDetail cardDetail) {
        this.cardDetail = cardDetail;
        this.cardType = cardDetail.getCardType();
        this.cardCategory = cardDetail.getCardCategory();
        this.vacuumRemaining = cardDetail.getVacuumRemaining();
        if (cardDetail.getCardCategory() == CardDetail.CardCategory.PARTNER) {
            balance = context.getString(R.string.cards_partners_balance_template, context.getString(R.string.cards_partners_balance_value));
            isBalanceDetailsVisible = false;
            isRemovable = false;
            switch (cardType) {
                case HBC:
                    cardImage = context.getDrawable(R.drawable.hudsons_bay_card);
                    cardName = context.getString(R.string.cards_hbc_label);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.HBC_CARD_FORMAT);
                    cardDescription = context.getString(R.string.cards_hbc_description);
                    break;
                case CAA:
                    cardImage = context.getDrawable(R.drawable.caa_card);
                    cardName = context.getString(R.string.cards_caa_label);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.PARTNER_CARD_FORMAT);
                    cardDescription = context.getString(R.string.cards_caa_description);
                    break;
                case BCAA:
                    cardImage = context.getDrawable(R.drawable.bcaa_card);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.PARTNER_CARD_FORMAT);
                    cardName = context.getString(R.string.cards_bcaa_label);
                    cardDescription = context.getString(R.string.cards_bcaa_description);
                    break;
                case RBC:
                    cardImage = context.getDrawable(R.drawable.rbc_card);
                    cardName = context.getString(R.string.cards_rbc_label);
                    cardDescription = context.getString(R.string.cards_rbc_description);
                    balanceDetails = context.getString(R.string.cards_rbc_balance_details);
                    isBalanceDetailsVisible = true;
                    break;
                case MORE:
                    cardImage = context.getDrawable(R.drawable.more_rewards_card);
                    cardName = context.getString(R.string.cards_more_label);
                    balance = context.getString(R.string.cards_partners_more_balance);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.PARTNER_CARD_FORMAT);
                    cardDescription = context.getString(R.string.cards_more_description);
                    break;
            }
        } else {
            int balanceValue = cardDetail.getBalance();
            this.balanceRemaining = cardDetail.getBalance();
            switch (cardType) {
                case PPTS:
                    cardImage = context.getDrawable(R.drawable.petro_points_card);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.PPTS_FORMAT);
                    cardName = context.getString(R.string.cards_ppts_label);
                    barCode = new BitmapDrawable(context.getResources(), generateBarcode(cardDetail));
                    balance = context.getString(R.string.cards_ppts_balance_template, CardFormatUtils.formatBalance(balanceValue));
                    balanceDetails = context.getString(R.string.cards_ppts_monetary_balance_template, CardFormatUtils.formatBalance(balanceValue / 1000));
                    isRemovable = false;
                    break;
                case FSR:
                    cardImage = context.getDrawable(cardDetail.getCpl() == 0.05f ? R.drawable.fsr_5cent_card : R.drawable.fsr_10cent_card);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.FSR_FORMAT);
                    cardName = context.getString(R.string.cards_fsr_expanded_label);
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_expanded_litres_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
                    int cpl = (int) (cardDetail.getCpl() * 100);
                    balanceDetails = context.getString(R.string.cards_fsr_balance_conversion, cpl);
                    cardDescription = context.getString(R.string.cards_fsr_description, cpl);
                    break;
                case SP:
                    cardImage = context.getDrawable(R.drawable.seasons_pass_card);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.WAG_SP_FORMAT);
                    cardName = context.getString(R.string.cards_sp_label);
                    balance = balanceValue != -1 ?
                             CardFormatUtils.formatBalance(balanceValue)
                            : null;
                    balanceDetails =  balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_days_left_expanded, balanceValue)
                            : null;
                    cardDescription = context.getString(R.string.cards_sp_description);
                    isBalanceDetailsVisible = false;
                    break;
                case WAG:
                    cardImage = context.getDrawable(R.drawable.wag_card);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.WAG_SP_FORMAT);
                    cardName = context.getString(R.string.cards_wag_expanded_label);
                    balance = balanceValue != -1 ?
                             CardFormatUtils.formatBalance(balanceValue)
                            : null;
                    balanceDetails =  balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_wash_left_expanded, balanceValue)
                            : null;
                    cardDescription = context.getString(R.string.cards_wag_description);
                    isBalanceDetailsVisible = false;
                    break;
                case PPC:
                    cardImage = context.getDrawable(R.drawable.preferred_price_card);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getCardNumber(), CardFormatUtils.PPC_FORMAT);
                    cardName = context.getString(R.string.cards_ppc_expanded_label);
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_expanded_litres_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
                    balanceDetails = context.getString(R.string.cards_ppc_balance_conversion, (int) (cardDetail.getCpl() * 100));
                    cardDescription = context.getString(R.string.cards_ppc_description);
                    break;
                case ST:
                    cardImage = context.getDrawable(R.drawable.member_small_cards_cw_ticket);
                    cardNumber = CardFormatUtils.formatForViewing(cardDetail.getTicketNumber(), CardFormatUtils.WAG_SP_FORMAT);
                    cardName = context.getString(R.string.single_ticket_card_label);
                    balance = balanceValue != -1 ?
                            context.getResources().getQuantityString(R.plurals.cards_washes_balance, balanceValue, CardFormatUtils.formatBalance(balanceValue))
                            : null;
                    cardDescription = context.getString(R.string.single_ticket_card_detail_description);
                    isBalanceDetailsVisible = false;
                    isRemovable = false;
                    break;
            }
        }
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public String getBalanceDetails() {
        return balanceDetails;
    }

    public CardDetail.CardCategory getCardCategory() {
        return cardCategory;
    }

    public String getBalance() {
        return balance;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Drawable getCardImage() {
        return cardImage;
    }

    public boolean isBalanceDetailsVisible() {
        return isBalanceDetailsVisible;
    }

    public Drawable getBarCode() {
        return barCode;
    }

    private Bitmap generateBarcode(CardDetail cardDetail) {
        String petroPointsCardNumber = cardDetail.getCardNumber();
        String dataForBarCode = petroPointsCardNumber;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            int width = 1500;
          int height = 330;
            BitMatrix bitMatrix = multiFormatWriter.encode(dataForBarCode, BarcodeFormat.PDF_417, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardDetail getCardDetail() {
        return cardDetail;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ExpandedCardItem)) {
            return false;
        }
        return ((ExpandedCardItem) obj).cardDetail.equals(getCardDetail());
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public String getVacuumRemaining() {
        return String.valueOf(vacuumRemaining);
    }

    public boolean isVacuumZero() {
        return vacuumRemaining == 0;
    }

    public void setVacuumRemaining(int vacuumRemaining) {
        this.vacuumRemaining = vacuumRemaining;
    }

    public boolean isWashBalanceZero() {
        return balanceRemaining == 0;
    }
}
