package com.firatnet.businessapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.VolleyMultipartRequest;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHOTO;
import static com.firatnet.businessapp.classes.URLTAG.SAVE_MP3;
import static com.firatnet.businessapp.classes.URLTAG.SAVE_PHOTO_URL;


public class UploadeMp3FileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = UploadeMp3FileActivity.class.getSimpleName();
    TextView fileName;
    Button fileBrowseBtn, uploadBtn;
    int REQ_CODE_PICK_SOUNDFILE = 1;
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    Uri fileUri;
    private File file;
    private File sourceFile;
    Context context;
    int totalSize = 0;
    // LinearLayout uploader_area;
    LinearLayout progress_area;
    private ProgressDialog progressDialog;
    public DonutProgress donut_progress;
    String realpath,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploade_mp3_file);

        fileName = findViewById(R.id.filepath);
        fileBrowseBtn = findViewById(R.id.select);
        uploadBtn = findViewById(R.id.upload);
        // uploader_area = findViewById(R.id.uploader_area);
        progress_area = findViewById(R.id.progress_area);
        donut_progress = findViewById(R.id.donut_progress);

        context = this;

        fileBrowseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(UploadeMp3FileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showFileChooserIntent();

                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(UploadeMp3FileActivity.this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceFile != null) {
                    PreferenceHelper helper = new PreferenceHelper(context);
                    id=helper.getSettingValueId();
                    new UploadFileToServer().execute();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please select a file first", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    /**
     * Shows an intent which has options from which user can choose the file like File manager, Gallery etc
     */
    private void showFileChooserIntent() {
        Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //Choose any file
        fileManagerIntent.setType("audio/mpeg");
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, UploadeMp3FileActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileChooserIntent();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    /**
     * Hides the Choose file button and displays the file preview, file name and upload button
     */
    private void hideFileChooser() {
        fileBrowseBtn.setVisibility(View.GONE);
        uploadBtn.setVisibility(View.VISIBLE);
        // fileName.setVisibility(View.VISIBLE);

    }

    /**
     * Displays Choose file button and Hides the file preview, file name and upload button
     */
    private void showFileChooser() {
        fileBrowseBtn.setVisibility(View.VISIBLE);
        uploadBtn.setVisibility(View.GONE);
        //   fileName.setVisibility(View.GONE);
    }

    /**
     * Show the file name and preview once the file is chosen
     *
     * @param uri
     */
    private void previewFile(Uri uri) {
        String filePath = getRealPathFromURIPath(uri, UploadeMp3FileActivity.this);
        sourceFile = new File(filePath);
        file = new File(filePath);
        Log.d(TAG, "Filename " + sourceFile.getName());
        fileName.setText(sourceFile.getName());

//        ContentResolver cR = this.getContentResolver();
//        String mime = cR.getType(uri);


        hideFileChooser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                Uri audioFileUri = data.getData();
                previewFile(audioFileUri);
                realpath = getRealPathFromURIPath(audioFileUri, UploadeMp3FileActivity.this);
                // Now you can use that Uri to get the file path, or upload it, ...
            }
        }
    }

    /**
     * Returns the actual path of the file in the file system
     *
     * @param contentURI
     * @param activity
     * @return
     */
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        String realPath = "";
        if (cursor == null) {
            realPath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            realPath = cursor.getString(idx);
        }
        if (cursor != null) {
            cursor.close();
        }

        return realPath;
    }


    //------------------------------------Uploade File
    private void uploadeFileServer(final String id) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading File.....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, SAVE_PHOTO_URL,
                new Response.Listener<NetworkResponse>() {


                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            progressDialog.dismiss();
                            if (obj.getBoolean("success")) {


                                //save photo url
                                PreferenceHelper helper = new PreferenceHelper(context);
                                helper.setSettingValuePhotoUrl(obj.getString("data"));

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(getApplicationContext(), "Error File not correct", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        try {

                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonObject = new JSONObject(responseBody);
                            Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            //Handle a malformed json response
                        } catch (UnsupportedEncodingException error2) {

                        }
                    }
                }) {
            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("Content-Type", "application/json; charset=utf-8");
                params.put(TAG_ID, id);

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                //long imagename = System.currentTimeMillis();
//                params.put(TAG_PHOTO, new DataPart(file.getName() , getFileData(file)));
//                return params;
//            }


        };

        //adding the request to volley

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


//    public byte[] getFileData(File file) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        File.Con
//        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();
//    }


    private class UploadFileToServer extends AsyncTask<String, String, String> {

        private HttpURLConnection httpConn;
        private DataOutputStream request;
        private final String boundary =  "*****";
        private final String crlf = "\r\n";
        private final String twoHyphens = "--";
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            donut_progress.setProgress(0);
            // uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFile = new File(realpath);
            totalSize = (int) sourceFile.length();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.d("PROG", progress[0]);
            donut_progress.setProgress(Integer.parseInt(progress[0])); //Updating progress
        }

        @Override
        protected String doInBackground(String... args) {

            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFile.getName();


            try {
                connection = (HttpURLConnection) new URL(SAVE_MP3).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true); // indicates POST method
                connection.setDoInput(true);
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"mp3\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: "+ URLConnection.guessContentTypeFromName(sourceFile.getName())+"\r\n"
                       // + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";


                String Header3 = "--" + boundary + this.crlf
                        + "Content-Disposition: form-data; name=\"id\""+this.crlf
                        + "Content-Type: application/json; charset=utf-8"+this.crlf
                        +this.crlf
                        +id+ this.crlf;
//                        + "Content-Type: application/json ; charset=UTF-8"+this.crlf
//                        + "Content-Type: text/plain ; charset=UTF-8"+this.crlf
                long fileLength = sourceFile.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader =  Header3 +fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;


                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();


                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();
//

                int progress = 0;
                int bytesRead = 0;
              //  int maxBufferSize = 1024 * 1024;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress("" + (int) ((progress * 100) / totalSize)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                InputStream stream  = null;
                try {
                    //Get Response
                stream = connection.getInputStream();
                    int responseCode = connection.getResponseCode();

                } catch (Exception e) {
                    try {
                        int responseCode = connection.getResponseCode();
                        String responseCode2 = connection.getResponseMessage();
                        Log.d(TAG,"responseCode : "+responseCode+" message"+responseCode2);
                        if (responseCode >= 400 && responseCode < 500)
                        {   stream = connection.getErrorStream();
                        Log.d(TAG,stream+""+responseCode);
                        }
                        else
                            throw e;
                    } catch (Exception es) {
                        es.printStackTrace();

                    }
                }
                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

            } catch (Exception e) {
                // Exception
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response", "Response from server: " + result);
            showFileChooser();
            super.onPostExecute(result);
        }

    }

}
