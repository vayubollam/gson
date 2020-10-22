package suncor.com.android.ui.main.wallet.cards.list;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.ui.common.ErrorView;

public class CardsErrorView extends BaseObservable implements ErrorView {
    private Context context;
    private Runnable buttonCallback;

    public CardsErrorView(Context context, Runnable buttonCallback) {
        this.context = context;
        this.buttonCallback = buttonCallback;
    }

    @Override
    @Bindable
    public String getTitle() {
        return context.getString(R.string.msg_cm001_title);
    }

    @Override
    @Bindable
    public String getMessage() {
        return context.getString(R.string.msg_cm001_message);
    }

    @Override
    public String getButtonText() {
        return context.getString(R.string.msg_cm001_button);
    }


    @Override
    public Runnable getButtonCallback() {
        notifyPropertyChanged(BR.title);
        notifyPropertyChanged(BR.message);
        return buttonCallback;
    }

    @Override
    public boolean hasErrorIcon() {
        return false;
    }
}
