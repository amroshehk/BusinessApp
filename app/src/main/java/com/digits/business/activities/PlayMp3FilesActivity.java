package com.digits.business.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.digits.business.R;
import com.digits.business.classes.PreferenceHelper;

//import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;

import static com.digits.business.classes.JsonTAG.TAG_BASIC;
import static com.digits.business.classes.JsonTAG.TAG_NAME;
import static com.digits.business.classes.JsonTAG.TAG_URL;

public class PlayMp3FilesActivity extends AppCompatActivity  implements JcPlayerManagerListener {
    JcPlayerView jcplayerView;
    Context context;
    String url,basic,name,pathToSave;
    private ProgressBar CircularProgress;
    private static final int REQUESTCODE_ACCESS_STORAGE = 7;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mp3_files);

        jcplayerView =  findViewById(R.id.jcplayer);
        CircularProgress = findViewById(R.id.progressbar_mp3file);
        context=this;

        url=getIntent().getStringExtra(TAG_URL);
        basic=getIntent().getStringExtra(TAG_BASIC);
        name=getIntent().getStringExtra(TAG_NAME);
        final String SDCardRoot = Environment.getExternalStorageDirectory().toString();
         pathToSave=SDCardRoot+"/MyVoiceMail";
        PreferenceHelper helper=new PreferenceHelper(context);
        helper.setSettingKeyLastRunMp3FileTitle(name);
        helper.setSettingKeyLastRunMp3File(url);

        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        if(basic!=null && !basic.equals(""))
        jcAudios.add(JcAudio.createFromFilePath(name,SDCardRoot+"/MyVoiceMail/voicemail.mp3"));
        else
        jcAudios.add(JcAudio.createFromURL(name,url));

        jcplayerView.initPlaylist(jcAudios, this);
        jcplayerView.setJcPlayerManagerListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //  jcplayerView.playAudio(JcAudio.createFromURL(name,url));
       if(basic!=null)
       { if(!basic.equals(""))
          {
              if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
              if(isPermissionsToAccessStorageAreGranted(context,PlayMp3FilesActivity.this))
              {
                  downloadFile();
              }
              else
              {
                  CircularProgress.setVisibility(View.GONE);
                  Toast.makeText(context,"Permission Canceled, Now your application cannot access VoiceMail.",Toast.LENGTH_LONG).show();
              }
              }
              else {
                  downloadFile();
              }
          }
          else {
                CircularProgress.setVisibility(View.GONE);
                jcplayerView.setVisibility(View.VISIBLE);
          }
       }
       else
       {
             CircularProgress.setVisibility(View.GONE);
             jcplayerView.setVisibility(View.VISIBLE);
       }

    }
    void downloadFile()
    {  // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(PlayMp3FilesActivity.this);
        downloadTask.execute("the url to the file you want to download");
    }
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            int downloadedSize = 0;
            int totalSize = 0;
            CircularProgress.setVisibility(View.VISIBLE);
            try {
                URL url_ = new URL(url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url_
                        .openConnection();
                final String basicAuth = "Basic " + Base64.encodeToString(basic.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty ("Authorization", basicAuth);
                urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);

                // connect
                //urlConnection.connect();

                File myDir;
                myDir = new File(pathToSave);
                myDir.mkdirs();

                // create a new file, to save the downloaded file

                String mFileName ="voicemail.mp3";
                File file = new File(myDir, mFileName);

                FileOutputStream fileOutput = new FileOutputStream(file);

                // Stream used for reading the data from the internet


                int status = urlConnection.getResponseCode();
                InputStream inputStream = urlConnection.getInputStream();
                if (status != HttpURLConnection.HTTP_OK)
                    inputStream = urlConnection.getErrorStream();
                else
                    inputStream = urlConnection.getInputStream();

                // this is the total size of the file which we are downloading
                totalSize = urlConnection.getContentLength();

                // runOnUiThread(new Runnable() {
                // public void run() {
                // pb.setMax(totalSize);
                // }
                // });

                // create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    // update the progressbar //
                    // runOnUiThread(new Runnable() {
                    // public void run() {
                    // pb.setProgress(downloadedSize);
                    // float per = ((float)downloadedSize/totalSize) * 100;
                    // cur_val.setText("Downloaded " + downloadedSize + "KB / " +
                    // totalSize + "KB (" + (int)per + "%)" );
                    // }
                    // });
                }
                // close the output stream when complete //

                fileOutput.close();


                // runOnUiThread(new Runnable() {
                // public void run() {
                // // pb.dismiss(); // if you want close it..
                // }
                // });

            } catch (final MalformedURLException e) {
                // showError("Error : MalformedURLException " + e);
                e.printStackTrace();
            } catch (final IOException e) {
                // showError("Error : IOException " + e);
                e.printStackTrace();
            } catch (final Exception e) {
                // showError("Error : Please check your internet connection " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null)
            { jcplayerView.setVisibility(View.VISIBLE);
            CircularProgress.setVisibility(View.GONE);}
           else
            {   Toast.makeText(context,"Voice mail error", Toast.LENGTH_LONG).show();
                CircularProgress.setVisibility(View.GONE);
            }

        }
    }
    void downloadFile(String dwnload_file_path, String fileName,
                             String pathToSave) {
        int downloadedSize = 0;
        int totalSize = 0;
        CircularProgress.setVisibility(View.VISIBLE);
        try {
            URL url = new URL(dwnload_file_path);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url
                    .openConnection();
            final String basicAuth = "Basic " + Base64.encodeToString(basic.getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty ("Authorization", basicAuth);
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);

            // connect
            //urlConnection.connect();

            File myDir;
            myDir = new File(pathToSave);
            myDir.mkdirs();

            // create a new file, to save the downloaded file

            String mFileName = fileName;
            File file = new File(myDir, mFileName);

            FileOutputStream fileOutput = new FileOutputStream(file);

            // Stream used for reading the data from the internet


            int status = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            if (status != HttpURLConnection.HTTP_OK)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            // this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            // runOnUiThread(new Runnable() {
            // public void run() {
            // pb.setMax(totalSize);
            // }
            // });

            // create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                // runOnUiThread(new Runnable() {
                // public void run() {
                // pb.setProgress(downloadedSize);
                // float per = ((float)downloadedSize/totalSize) * 100;
                // cur_val.setText("Downloaded " + downloadedSize + "KB / " +
                // totalSize + "KB (" + (int)per + "%)" );
                // }
                // });
            }
            // close the output stream when complete //
            CircularProgress.setVisibility(View.GONE);
            jcplayerView.setVisibility(View.VISIBLE);
            fileOutput.close();
            // runOnUiThread(new Runnable() {
            // public void run() {
            // // pb.dismiss(); // if you want close it..
            // }
            // });

        } catch (final MalformedURLException e) {
            // showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            // showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            // showError("Error : Please check your internet connection " + e);
        }
    }
    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onContinueAudio( JcStatus jcStatus) {

    }

    @Override
    public void onJcpError( Throwable throwable) {

    }

    @Override
    public void onPaused( JcStatus jcStatus) {

    }

    @Override
    public void onPlaying( JcStatus jcStatus) {
        jcplayerView.createNotification();
    }

    @Override
    public void onPreparedAudio( JcStatus jcStatus) {

    }

    @Override
    public void onStopped( JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged( JcStatus jcStatus) {

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

    public static boolean isPermissionsToAccessStorageAreGranted(Context context, Activity activity) {
        ArrayList<String> permissions = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {
            String[] permissionsList = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(activity, permissionsList,
                    REQUESTCODE_ACCESS_STORAGE);
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUESTCODE_ACCESS_STORAGE && grantResults[0]== PackageManager.PERMISSION_GRANTED){
             downloadFile();
        }
    }
}
