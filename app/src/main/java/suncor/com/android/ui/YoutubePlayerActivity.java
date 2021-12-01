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
import com.google.firebase.analytics.FirebaseAnalytics;

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
    private boolean show25 = false, show50 = false, show75 = false;
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
            //do nothing
        }

        @Override
        public void onSeekTo(int i) {
            //do nothing
        }
    };

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
            //do nothing
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            //do nothing
        }

        @Override
        public void onLoaded(String arg0) {
            //do nothing
        }

        @Override
        public void onLoading() {
            //do nothing
        }

        @Override
        public void onVideoEnded() {
            AnalyticsUtils.logEvent(getApplication().getBaseContext(), AnalyticsUtils.Event.videoStart, new Pair<>(AnalyticsUtils.Param.videoTitle, videoTitle));

        }

        @Override
        public void onVideoStarted() {
            AnalyticsUtils.logEvent(getApplication().getBaseContext(), AnalyticsUtils.Event.videoComplete, new Pair<>(AnalyticsUtils.Param.videoTitle, videoTitle));
        }
    };
    private void displayCurrentTime() {
        if (null == mPlayer) return;
        if ( mPlayer.getCurrentTimeMillis() != 0){
            int percentage =  ( mPlayer.getCurrentTimeMillis() * 100 ) / mPlayer.getDurationMillis();
            if (percentage > 25 && !show25) {
                show25 = true;
                AnalyticsUtils.logEvent(getApplication().getBaseContext(), AnalyticsUtils.Event.videoThreshold25, new Pair<>(AnalyticsUtils.Param.videoTitle, videoTitle));
            } else if (percentage > 50 && !show50) {
                show50 = true;
                AnalyticsUtils.logEvent(getApplication().getBaseContext(), AnalyticsUtils.Event.videoThreshold50, new Pair<>(AnalyticsUtils.Param.videoTitle, videoTitle));
            } else if (percentage > 75 && !show75) {
                show75 = true;
                AnalyticsUtils.logEvent(getApplication().getBaseContext(), AnalyticsUtils.Event.videoThreshold75, new Pair<>(AnalyticsUtils.Param.videoTitle, videoTitle));
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
        //do nothing
    }
}
