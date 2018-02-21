package com.abdelmun3m.movie_land.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.abdelmun3m.movie_land.GeneralData;
import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.utilities.YouTubeFailureRecoveryActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MovieDisplayActivity extends YouTubeFailureRecoveryActivity implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        YouTubePlayer.OnFullscreenListener {

    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    @BindView(R.id.layout)
        LinearLayout baseLayout;
    @BindView(R.id.player)
        YouTubePlayerView playerView;

    private YouTubePlayer player;
    private boolean fullscreen;
    private java.lang.String videoKey ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_display);

        Intent i = getIntent();
        String key = this.getString(R.string.movie_Video_Key);

        if(i.hasExtra(key)){
            ButterKnife.bind(this);
            videoKey = i.getStringExtra(key);
            playerView.initialize(GeneralData.YOUTUP_KEY, this);
        }else{

            //TODO Show error Message Key
        }
    }

    @Override
    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.error_Youtube))
                .setPositiveButton(getString(R.string.md_open_browser), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openWebPage(videoKey);
                    }
                }).setTitle(getString(R.string.md_dialog_title));
        builder.show();
    }

    public void openWebPage(String videoKey) {
        Uri webpage = Uri.parse(GeneralData.YOUTUBE_MOVIE+videoKey);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return playerView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.player = youTubePlayer;
        // Specify that we want to handle fullscreen behavior ourselves.
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        player.setOnFullscreenListener(this);
        if (!wasRestored) {
            player.cueVideo(videoKey);
        }
    }

    @Override
    public void onClick(View v) {
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
    @Override
    public void onFullscreen(boolean isFullscreen) {
        //fullscreen = isFullscreen;
        //doLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private void doLayout() {

        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) playerView.getLayoutParams();
        if (fullscreen) {
            // When in fullscreen, the visibility of all other views than the player should be set to
            // GONE and the player should be laid out across the whole screen.
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;

        } else {
            // This layout is up to you - this is just a simple example (vertically stacked boxes in
            // portrait, horizontally stacked in landscape).
            }
    }



}
