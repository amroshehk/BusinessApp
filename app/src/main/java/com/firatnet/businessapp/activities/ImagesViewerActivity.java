package com.firatnet.businessapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firatnet.businessapp.BuildConfig;
import com.firatnet.businessapp.R;
import com.firatnet.businessapp.classes.GalleryUtil;
import com.firatnet.businessapp.classes.PreferenceHelper;
import com.firatnet.businessapp.classes.ProcessProfilePhotoTask;
import com.firatnet.businessapp.classes.VolleyMultipartRequest;
import com.firatnet.businessapp.entities.Register;
import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.firatnet.businessapp.classes.JsonTAG.TAG_ID;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NAME;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NEW_EMAIL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NEW_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_NEW_PHONE;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PASSWORD;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHONE;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHOTO;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_PHOTO_URL;
import static com.firatnet.businessapp.classes.JsonTAG.TAG_RESULTS;
import static com.firatnet.businessapp.classes.URLTAG.REGISTER_URL;
import static com.firatnet.businessapp.classes.URLTAG.SAVE_PHOTO_URL;
import static com.firatnet.businessapp.classes.URLTAG.UPDATE_USER_URL;

public class ImagesViewerActivity extends AppCompatActivity {
    Context context;

    //sign up
    String filePath;
    private Bitmap bitmap;

    ImageButton pic;
    PhotoView photoView;
    private final int GALLERY_ACTIVITY_CODE = 200;
    private final int RESULT_CROP = 400;
    private static final String TEMP_PHOTO_FILE = "tempPhoto.jpg";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_viewer);
        photoView =  findViewById(R.id.photo_view);
        pic =  findViewById(R.id.pic);
        ImageView close = findViewById(R.id.close);
        context=this;



        PreferenceHelper helper=new PreferenceHelper(context);


        String image_url=helper.getSettingValuePhotoUrl() ;
        ImageLoaderConfiguration config;
        ImageLoader imageLoader = ImageLoader.getInstance();
        config= new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        if (!helper.getSettingValuePhotoUrl().isEmpty())
            imageLoader.displayImage(image_url, photoView, options);
        else
            photoView.setBackgroundResource(R.drawable.user512);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.user512);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(ImagesViewerActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    imageBrowse();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ImagesViewerActivity.this, "Permission denied to Write on your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
//
//            //getting the image Uri
//            Uri imageUri = data.getData();
//            try {
//                //getting bitmap object from uri
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//
//                //displaying selected image to imageview
//
//                filePath = getPath(imageUri);
//                ChangeImageServer(student_form_db.getId(),bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String    picturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }

        if (requestCode == RESULT_CROP ) {
            if(resultCode == Activity.RESULT_OK){
                //   Bundle extras = data.getExtras();
                //  bitmap = extras.getParcelable("data");
                // Set The Bitmap Data To ImageView

                File tempFile = getTempFile();
                processPhotoUpdate(tempFile);


            }
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentUri = FileProvider.getUriForFile(ImagesViewerActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        f);

            } else {
                contentUri = Uri.fromFile(f);
            }
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);
            cropIntent.putExtra("scale", true);

            // retrieve data on return
            cropIntent.putExtra("return-data", false);
            // start the activity - we handle returning in onActivityResult
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Unfortunately your device does not support cropping image";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {
        if (isSDCARDMounted()) {

            File folder = new File(Environment.getExternalStorageDirectory(), "Businessapp");//create folder
            folder.mkdir();

            File f = new File(Environment.getExternalStorageDirectory()+"/Businessapp/" ,TEMP_PHOTO_FILE);
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Toast.makeText(this, "File IO issue, can\\'t write temp file on this system", Toast.LENGTH_LONG).show();
            }
            return f;
        } else {
            return null;
        }
    }

    private boolean isSDCARDMounted(){
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
    /*
     *  processes a temp photo file from
     */
    private void processPhotoUpdate(File tempFile) {
        ProcessProfilePhotoTask task = new ProcessProfilePhotoTask(){

            @Override
            protected void onPostExecute(Bitmap result) {
                bitmap=result;


                PreferenceHelper helper = new PreferenceHelper(context);
                String id=helper.getSettingValueId();
                  ChangeImageServer(id,bitmap);
                //pic.setImageBitmap(bitmap);
            }

        };
        task.execute(tempFile);

    }
    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(),    contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    private void imageBrowse() {
        //if everything is ok we will open image chooser
        //  Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //  startActivityForResult(i, 100);
        Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
        startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
    }

    //------------------------------------Edit image
    private void ChangeImageServer(final String id,final Bitmap bitmap) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Edit photo.....");
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
                            if(obj.getBoolean("success"))
                            {
                                ImageLoader.getInstance().clearDiskCache();
                                ImageLoader.getInstance().clearMemoryCache();

                                photoView.setImageBitmap(bitmap);
                                photoView.setScaleType(CircleImageView.ScaleType.CENTER_CROP);

                                //save photo url
                                PreferenceHelper helper = new PreferenceHelper(context);
                                helper.setSettingValuePhotoUrl(obj.getString("data"));

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                            else {

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

                            String responseBody = new String( error.networkResponse.data, "utf-8" );
                            JSONObject jsonObject = new JSONObject( responseBody );
                            Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        } catch ( JSONException e ) {
                            //Handle a malformed json response
                        } catch (UnsupportedEncodingException error2){

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
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put(TAG_PHOTO, new DataPart(imagename + ".jpeg", getFileDataFromDrawable(bitmap)));
                return params;
            }



        };

        //adding the request to volley

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
