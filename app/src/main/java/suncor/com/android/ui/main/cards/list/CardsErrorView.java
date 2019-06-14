package suncor.com.android.ui.main.cards.list;

import android.content.Context;

import androidx.databinding.Bindable;
import suncor.com.android.ui.common.GenericErrorView;

public class CardsErrorView extends GenericErrorView {
    public CardsErrorView(Context context, int buttonText, Runnable buttonCallback) {
        super(context, buttonText, buttonCallback);
    }


    @Override
    public boolean hasErrorIcon() {
        return false;
    }
}
