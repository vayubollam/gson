package suncor.com.android.ui.common;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;

public interface ErrorView extends Observable {
    @Bindable
    String getTitle();

    @Bindable
    String getMessage();

    String getButtonText();

    Runnable getButtonCallback();
}