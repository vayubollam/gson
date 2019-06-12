package suncor.com.android.ui;

import android.os.Bundle;
import android.view.View;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import suncor.com.android.BuildConfig;
import suncor.com.android.R;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String VIDEO_ID_EXTRA = "video-id";

    private YouTubePlayerView youtubePlayer;
    private String videoId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.activity_youtube_player);
        youtubePlayer = findViewById(R.id.youtube_player);
        youtubePlayer.initialize(BuildConfig.GOOGLE_API_KEY, this);
        if (!getIntent().hasExtra(VIDEO_ID_EXTRA)) {
            throw new IllegalStateException("You should pass the video id as extra");
        }
        videoId = getIntent().getStringExtra(VIDEO_ID_EXTRA);
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
