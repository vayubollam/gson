package suncor.com.android.ui.common;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.utilities.ConnectionUtil;

public class GenericErrorView extends BaseObservable implements ErrorView {

    private Context context;
    private int buttonText;
    private Runnable buttonCallback;

    public GenericErrorView(Context context, int buttonText, Runnable buttonCallback) {
        this.context = context;
        this.buttonText = buttonText;
        this.buttonCallback = buttonCallback;
    }

    @Override
    public boolean hasErrorIcon() {
        return true;
    }

    @Override
    @Bindable
    public String getTitle() {
        return ConnectionUtil.haveNetworkConnection(context) ? context.getString(R.string.msg_e001_title) : context.getString(R.string.msg_e002_title);
    }

    @Override
    @Bindable
    public String getMessage() {
        return ConnectionUtil.haveNetworkConnection(context) ? context.getString(R.string.msg_e001_message) : context.getString(R.string.msg_e002_message);
    }

    @Override
    public String getButtonText() {
        return context.getString(buttonText);
    }


    @Override
    public Runnable getButtonCallback() {
        notifyPropertyChanged(BR.title);
        notifyPropertyChanged(BR.message);
        return buttonCallback;
    }
}
