package com.firatnet.businessapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.firatnet.businessapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_URL;

public class PlayMp3FilesActivity extends AppCompatActivity  implements JcPlayerManagerListener {
    JcPlayerView jcplayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mp3_files);

        jcplayerView =  findViewById(R.id.jcplayer);

        String url=getIntent().getStringExtra(TAG_URL);
        String name=getIntent().getStringExtra(TAG_NAME);

       // ArrayList<JcAudio> jcAudios = new ArrayList<>();
       // jcAudios.add(JcAudio.createFromURL(name,url));

        //jcplayerView.initPlaylist(jcAudios, null);
        jcplayerView.setJcPlayerManagerListener(this);
        jcplayerView.playAudio(JcAudio.createFromURL(name,url));
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
        jcplayerView.pause();
        jcplayerView.createNotification();
        //save current file url and name to shared
    }
}
