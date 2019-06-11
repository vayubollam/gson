package suncor.com.android.ui.common;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.ui.main.MainActivity;

public class YoutubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {

    private View view;
    private String videoId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        videoId = YoutubeFragmentArgs.fromBundle(getArguments()).getVideoId();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = super.onCreateView(layoutInflater, viewGroup, bundle);
        FrameLayout layout = (FrameLayout) layoutInflater.inflate(R.layout.fragment_youtube, viewGroup, false);
        layout.addView(view);
        initialize(BuildConfig.GOOGLE_API_KEY, this);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        //force hiding the bottomnavigation, otherwise Youtube thinks that it has an overlay on top
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        mainActivity.findViewById(R.id.mainDivider).setVisibility(View.GONE);

        int flags = mainActivity.getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        mainActivity.getWindow().getDecorView().setSystemUiVisibility(flags);

        mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
        mainActivity.findViewById(R.id.mainDivider).setVisibility(View.VISIBLE);
        int flags = mainActivity.getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        mainActivity.getWindow().getDecorView().setSystemUiVisibility(flags);

        mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setShowFullscreenButton(false);
        if (!wasRestored) {
            youTubePlayer.loadVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
