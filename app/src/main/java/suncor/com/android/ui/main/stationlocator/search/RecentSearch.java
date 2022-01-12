package suncor.com.android.ui.main.stationlocator.search;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.Nullable;

public class RecentSearch {
    private String primaryText;
    private String secondaryText;
    private LatLng coordinate;
    private String placeId;


    public RecentSearch(String primaryText, String secondaryText, LatLng coordinate, String placeId) {
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
        this.coordinate = coordinate;
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPrimaryText() {
        return primaryText;
    }


    public String getSecondaryText() {
        return secondaryText;
    }


    public LatLng getCoordinate() {
        return coordinate;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if( (obj instanceof RecentSearch && ((RecentSearch) obj).placeId != null))
            return (((RecentSearch) obj).placeId.equals(this.placeId));
        return false;
    }
}
