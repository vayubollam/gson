package suncor.com.android.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class PlaceSuggestion extends BaseObservable {

    private String primaryText;
    private String secondaryText;
    private String placeId;

    public PlaceSuggestion( String primaryText, String secondaryText, String placeId) {
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
        this.placeId = placeId;
    }


@Bindable
    public String getPrimaryText() {
        return primaryText;
    }
@Bindable
    public String getSecondaryText() {
        return secondaryText;
    }
@Bindable
    public String getPlaceId() {
        return placeId;
    }


    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
        notifyPropertyChanged(suncor.com.android.BR.primaryText);
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
