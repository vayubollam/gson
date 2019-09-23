package suncor.com.android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.utilities.AnalyticsUtils;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String VIDEO_ID_EXTRA = "video-id";
    public static final String VIDEO_TITLE = "video-title";
    private YouTubePlayer mPlayer;
    private YouTubePlayerView youtubePlayer;
    private String videoId;
    private String videoTitle;
    Handler mHandler;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_youtube_player);
        youtubePlayer = findViewById(R.id.youtube_player);
        youtubePlayer.initialize(BuildConfig.GOOGLE_API_KEY, this);
        if (!getIntent().hasExtra(VIDEO_ID_EXTRA)) {
            throw new IllegalStateException("You should pass the video id as extra");
        }
        videoId = getIntent().getStringExtra(VIDEO_ID_EXTRA);
        videoTitle = getIntent().getStringExtra(VIDEO_TITLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(this, "youtube-player");

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setShowFullscreenButton(false);
        mPlayer = youTubePlayer;
        if (videoId != null && !videoId.equals(this.videoId)) {
            this.videoId = videoId;
            if (youtubePlayer != null) {
                try {
                    mPlayer.loadVideo(videoId);
                } catch (IllegalStateException e) {
                    youtubePlayer.initialize(BuildConfig.GOOGLE_API_KEY, this);
                }
            }
        }
        if (!wasRestored) {
            youTubePlayer.loadVideo(videoId);

        }
        mHandler = new Handler();
        mPlayer.setPlaybackEventListener(mPlaybackEventListener);
        mPlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);
    }
    public YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            mHandler.postDelayed(runnable, 1);
    }

        @Override
        public void onPaused() {
            mHandler.removeCallbacks(runnable);
        }

        @Override
        public void onStopped() {
            mHandler.removeCallbacks(runnable);

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
            AnalyticsUtils.logEvent(getApplication().getBaseContext(), "video_complete", new Pair<>("videoTitle", videoTitle));

        }

        @Override
        public void onVideoStarted() {
            AnalyticsUtils.logEvent(getApplication().getBaseContext(), "video_start", new Pair<>("videoTitle",  videoTitle));
        }
    };
    private void displayCurrentTime() {
        if (null == mPlayer) return;
        if ( mPlayer.getCurrentTimeMillis() != 0){
        int pourcentage =  ( mPlayer.getCurrentTimeMillis() * 100 ) / mPlayer.getDurationMillis();

            if ( pourcentage == 25 || pourcentage == 50|| pourcentage == 75  ){
                String  pour = "video_threshold_"+ Integer.toString(pourcentage) ;
                AnalyticsUtils.logEvent(getApplication().getBaseContext(), pour, new Pair<>("videoTitle", videoTitle));
    }
        }
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayCurrentTime();
            mHandler.postDelayed(this, 1000);
        }
    };
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
