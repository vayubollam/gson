package suncor.com.android.model.cards;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.text.NumberFormat;
import java.util.Locale;

import suncor.com.android.R;

public class TransactionDetail {
    private Transaction transaction;
    private Context context;

    public TransactionDetail(Transaction transaction, Context context) {
        this.transaction = transaction;
        this.context = context;
    }

    public SpannableString getFormattedTitle() {
        String formattedTootlePoints = NumberFormat.getNumberInstance(Locale.getDefault()).format(transaction.getTotalPoints());
        String title = "";
        switch (transaction.getTransactionType()) {
            case REDEMPTION:
                title = context.getResources().getString(R.string.transaction_title_redemption);
                break;
            case VOID:
                title = context.getResources().getString(R.string.transaction_title_pp);
                break;
            case CUSTOMER_SERVICE_ADJ:
            case PETRO_POINTS:
            case PARTNER_POINTS_TRANSFER:
                title = context.getResources().getString(R.string.transaction_title_pp);
                break;
            case PURCHASE:
            case BONUS:
                title = context.getResources().getString(R.string.transaction_title_pe);
                break;
        }
        SpannableString titleSpan = new SpannableString(formattedTootlePoints + " " + title);
        titleSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, formattedTootlePoints.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return titleSpan;

    }

    public String getFormattedBasePoints() {
        String basePoint = NumberFormat.getNumberInstance(Locale.getDefault()).format(transaction.getBasePoints());
        switch (transaction.getTransactionType()) {
            case BONUS:
            case PURCHASE:
                return basePoint;
            case REDEMPTION:
            case VOID:
            case PARTNER_POINTS_TRANSFER:
                return basePoint;
            default:
                return transaction.getBasePoints() > 0 ? "+" + basePoint : basePoint;

        }
    }

    public String getFormattedBonusPoints() {
        String bonusPoint = NumberFormat.getNumberInstance(Locale.getDefault()).format(transaction.getBonusPoints());
        switch (transaction.getTransactionType()) {
            case PURCHASE:
            case CUSTOMER_SERVICE_ADJ:
            case BONUS:
            case VOID:
            case PETRO_POINTS:
                return transaction.getBonusPoints() > 0 ? "+" + bonusPoint : bonusPoint;
            case PARTNER_POINTS_TRANSFER:
                return "0";
            case REDEMPTION:
                return getTransaction().getBonusPoints() > 0 ? "+" + bonusPoint : "0";
            default:
                return bonusPoint;
        }
    }


    public String getFormattedRedeemPoints() {
        String redeemPoint = NumberFormat.getNumberInstance(Locale.getDefault()).format(transaction.getRedeemPointsInt());

        switch (transaction.getTransactionType()) {

            case PURCHASE:
            case CUSTOMER_SERVICE_ADJ:
            case BONUS:
            case VOID:
            case PETRO_POINTS:
            case PARTNER_POINTS_TRANSFER:
            case REDEMPTION:
                return redeemPoint;
            default:
                return redeemPoint;
        }
    }


    public String getFormattedTransactionType() {
        switch (transaction.getTransactionType()) {
            case PARTNER_POINTS_TRANSFER:
                return context.getResources().getString(R.string.transaction_type_ppt);
            case BONUS://
                return context.getResources().getString(R.string.transaction_type_bonus);
            case PETRO_POINTS://
                return context.getResources().getString(R.string.transaction_type_points);
            case CUSTOMER_SERVICE_ADJ://
                return context.getResources().getString(R.string.transaction_type_csa);
            case PURCHASE:
                return context.getResources().getString(R.string.transaction_type_purchase);
            case REDEMPTION://
                return context.getResources().getString(R.string.transaction_type_redemption);
            case VOID:
                return context.getResources().getString(R.string.transaction_type_void);
            default:
                return null;
        }

    }

    public Transaction getTransaction() {
        return transaction;
    }
}
