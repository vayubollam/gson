package suncor.com.android.model.cards;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
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
        String formattdBasePoints = NumberFormat.getNumberInstance(Locale.getDefault()).format(transaction.getBasePoints());
        String title = "";
        switch (transaction.getTransactionType()) {
            case REDEMPTION:
                title = context.getResources().getString(R.string.transaction_title_redemption);
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
        String points = transaction.getTransactionType() == Transaction.TransactionType.REDEMPTION || transaction.getTransactionType() == Transaction.TransactionType.PARTNER_POINTS_TRANSFER ? formattdBasePoints : formattedTootlePoints;
        SpannableString titleSpan = new SpannableString(points + " " + title);
        titleSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, points.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return titleSpan;

    }

    public String getFormattedBasePoints() {
        String basePoint = NumberFormat.getNumberInstance(Locale.getDefault()).format(transaction.getBasePoints());
        switch (transaction.getTransactionType()) {
            case BONUS:
            case PURCHASE:
                return basePoint;
            case REDEMPTION:
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

    public String getFormattedTransactionType() {
        switch (transaction.getTransactionType()) {
            case PARTNER_POINTS_TRANSFER:
                return context.getResources().getString(R.string.transaction_type_ppt);
            case BONUS:
                return context.getResources().getString(R.string.transaction_type_bonus);
            case PETRO_POINTS:
            case CUSTOMER_SERVICE_ADJ:
                return context.getResources().getString(R.string.transaction_type_csa);
            case PURCHASE:
                return context.getResources().getString(R.string.transaction_type_purchase);
            case REDEMPTION:
                return context.getResources().getString(R.string.transaction_type_redemption);
            default:
                return null;
        }

    }

    public Transaction getTransaction() {
        return transaction;
    }
}
