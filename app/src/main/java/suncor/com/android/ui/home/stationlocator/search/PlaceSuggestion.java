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
        notifyPropertyChanged(suncor.com.android.BR.secondaryText);
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
