package suncor.com.android.ui.home.stationlocator;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.SupportMapFragment;

import suncor.com.android.R;

public class NavigationSupportMapFragment extends SupportMapFragment {

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(getContext(), R.anim.dummy);
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
}
