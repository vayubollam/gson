package suncor.com.android.ui.home.stationlocator.search;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class PlaceSuggestion extends BaseObservable {

    private String primaryText;
    private String secondaryText;
    private String placeId;


    @Bindable
    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
        notifyPropertyChanged(suncor.com.android.BR.primaryText);
    }

    @Bindable
    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
        notifyPropertyChanged(suncor.com.android.BR.secondaryText);
    }

    @Bindable
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
