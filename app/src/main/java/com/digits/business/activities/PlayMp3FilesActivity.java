package com.digits.business.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;


import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.digits.business.R;
import com.digits.business.classes.PreferenceHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.digits.business.classes.JsonTAG.TAG_NAME;
import static com.digits.business.classes.JsonTAG.TAG_URL;

public class PlayMp3FilesActivity extends AppCompatActivity  implements JcPlayerManagerListener {
    JcPlayerView jcplayerView;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mp3_files);

        jcplayerView =  findViewById(R.id.jcplayer);
        context=this;

        String url=getIntent().getStringExtra(TAG_URL);
        String name=getIntent().getStringExtra(TAG_NAME);

        PreferenceHelper helper=new PreferenceHelper(context);
        helper.setSettingKeyLastRunMp3FileTitle(name);
        helper.setSettingKeyLastRunMp3File(url);

        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        jcAudios.add(JcAudio.createFromURL(name,url));

        jcplayerView.initPlaylist(jcAudios, this);
        jcplayerView.setJcPlayerManagerListener(this);

      //  jcplayerView.playAudio(JcAudio.createFromURL(name,url));
    }


    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onContinueAudio(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onJcpError(@NotNull Throwable throwable) {

    }

    @Override
    public void onPaused(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onPlaying(@NotNull JcStatus jcStatus) {
        jcplayerView.createNotification();
    }

    @Override
    public void onPreparedAudio(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onStopped(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        jcplayerView.pause();
        jcplayerView.createNotification();

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceHelper helper=new PreferenceHelper(context);

        String url=helper.getSettingKeyLastRunMp3File();
        String name=helper.getSettingKeyLastRunMp3FileTitle();

        jcplayerView.pause();
        jcplayerView.createNotification();
        //save current file url and name to shared
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jcplayerView.kill();
    }
}
