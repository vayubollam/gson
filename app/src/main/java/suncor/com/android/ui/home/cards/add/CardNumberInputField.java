package suncor.com.android.ui.home.cards.add;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;
import suncor.com.android.ui.common.cards.CardFormat;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.common.input.InputField;

public class CardNumberInputField extends InputField {

    @StringRes
    private int formatError;
    private Runnable changedListener;

    public CardNumberInputField(@StringRes int error, @StringRes int formatError) {
        super(error);
        this.formatError = formatError;
    }

    public void setChangedListener(Runnable changedListener) {
        this.changedListener = changedListener;
    }

    @Bindable
    @Override
    public int getError() {
        if (!getShowError() || isValid()) {
            return -1;
        } else {
            return isEmpty() ? super.getError() : formatError;
        }
    }

    @Override
    public void setText(String text) {
        if (!text.equals(this.getText()) && changedListener != null) {
            changedListener.run();
        }
        super.setText(text);
    }

    @Override
    public boolean isValid() {
        return !isEmpty() && isFormatValid();
    }

    private boolean isFormatValid() {
        CardFormat format = CardFormatUtils.findCardFormat(getText());
        return format != null && getText().replace(" ", "").length() == format.getLength();
    }
}
