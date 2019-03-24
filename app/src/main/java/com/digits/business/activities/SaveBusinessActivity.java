package com.digits.business.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.digits.business.BuildConfig;
import com.digits.business.R;
import com.digits.business.classes.GalleryUtil;
import com.digits.business.classes.PreferenceHelper;
import com.digits.business.classes.ProcessProfilePhotoTask;
import com.digits.business.classes.StaticMethod;
import com.digits.business.classes.VolleyMultipartRequest;
import com.digits.business.entities.Business;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.digits.business.classes.BitmapUtils.convertBitmapToByteArray;
import static com.digits.business.classes.JsonTAG.BUSINESS_ADDRESS;
import static com.digits.business.classes.JsonTAG.BUSINESS_CITY;
import static com.digits.business.classes.JsonTAG.BUSINESS_COUNTRY;
import static com.digits.business.classes.JsonTAG.BUSINESS_EMPLOYESS_NUMBER;
import static com.digits.business.classes.JsonTAG.BUSINESS_ID;
import static com.digits.business.classes.JsonTAG.BUSINESS_KEYWORDS;
import static com.digits.business.classes.JsonTAG.BUSINESS_NAME;
import static com.digits.business.classes.JsonTAG.BUSINESS_PARTNERSHIP_TYPE;
import static com.digits.business.classes.JsonTAG.BUSINESS_PIN_CODE;
import static com.digits.business.classes.JsonTAG.BUSINESS_PRODUCTS;
import static com.digits.business.classes.JsonTAG.BUSINESS_TURNOVER;
import static com.digits.business.classes.JsonTAG.BUSINESS_TYPE;
import static com.digits.business.classes.JsonTAG.BUSINESS_YEAR_ESTABLISHED;
import static com.digits.business.classes.URLTAG.URL_SAVE_BUSINESS;



public class SaveBusinessActivity extends AppCompatActivity {

    private String userId;

    private Context context;

    private ProgressDialog progressDialog;

    private TextInputEditText businessNameET;
    private TextInputEditText businessTypeET;
    private TextInputEditText partnershipTypeET;
    private TextInputEditText yearEstablishedET;
    private TextInputEditText employeesNumberET;
    private TextInputEditText turnoverET;
    private TextInputEditText addressET;
    private TextInputEditText countryET;
    private TextInputEditText cityET;
    private TextInputEditText pinCodeET;
    private TextInputEditText productsET;
    private TextInputEditText keywordsET;



    private final int GALLERY_ACTIVITY_CODE = 200;
    private final int RESULT_CROP = 400;
    private static final String TEMP_PHOTO_FILE = "tempPhoto.jpg";
    private Bitmap bitmap;


    private TextView errors;

    private CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_business);

        context = this;

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        userId = preferenceHelper.getSettingValueId();

        image = findViewById(R.id.photo);

        businessNameET = findViewById(R.id.businessName);
        businessTypeET = findViewById(R.id.businessType);
        partnershipTypeET = findViewById(R.id.partnershipType);
        yearEstablishedET = findViewById(R.id.yearEstablished);
        employeesNumberET = findViewById(R.id.employeesNumber);
        turnoverET = findViewById(R.id.turnover);
        addressET = findViewById(R.id.address);
        countryET = findViewById(R.id.country);
        cityET = findViewById(R.id.city);
        pinCodeET = findViewById(R.id.pinCode);
        productsET = findViewById(R.id.productsAndServices);
        keywordsET = findViewById(R.id.keywords);

        errors = findViewById(R.id.errors);

        Button saveBusiness = findViewById(R.id.saveBusiness);
        saveBusiness.setOnClickListener(saveClickListener);

        image.setOnClickListener(selectYourImageListener);

    }


    private View.OnClickListener selectYourImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(SaveBusinessActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    };



    // select Image
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    Toast.makeText(SaveBusinessActivity.this,
                            "Permission denied to Write on your External storage",
                            Toast.LENGTH_SHORT).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String picturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }

        if (requestCode == RESULT_CROP) {
            if (resultCode == Activity.RESULT_OK) {

                File tempFile = getTempFile();
                processPhotoUpdate(tempFile);

            }
        }

    }


    private void imageBrowse() {
        //if everything is ok we will open image chooser
        //  Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //  startActivityForResult(i, 100);
        Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
        startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
    }


    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentUri = FileProvider.getUriForFile(SaveBusinessActivity.this,
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



    private File getTempFile() {
        if (isSDCARDMounted()) {

            File folder = new File(Environment.getExternalStorageDirectory(), "9digits");//create folder
            folder.mkdir();

            File f = new File(Environment.getExternalStorageDirectory() + "/9digits/", TEMP_PHOTO_FILE);
            try {
                f.createNewFile();
            } catch (IOException e) {
                Toast.makeText(this, "File IO issue, can\\'t write temp file on this system", Toast.LENGTH_LONG).show();
            }
            return f;
        } else {
            return null;
        }
    }



    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }



    private void processPhotoUpdate(File tempFile) {
        @SuppressLint("StaticFieldLeak") ProcessProfilePhotoTask task = new ProcessProfilePhotoTask() {

            @Override
            protected void onPostExecute(Bitmap result) {
                bitmap = result;
                image.setImageBitmap(bitmap);
            }

        };
        task.execute(tempFile);


    }



    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }



    private View.OnClickListener saveClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String name = businessNameET.getText().toString();
            String businessType = businessTypeET.getText().toString();
            String partnershipType = partnershipTypeET.getText().toString();
            String yearEstablished = yearEstablishedET.getText().toString();
            String employeesNumber = employeesNumberET.getText().toString();
            String turnover = turnoverET.getText().toString();
            String address = addressET.getText().toString();
            String country = countryET.getText().toString();
            String city = cityET.getText().toString();
            String pinCode = pinCodeET.getText().toString();
            String products = productsET.getText().toString();
            String keywords = keywordsET.getText().toString();

            errors.setText(" ");

            if ( name.isEmpty() || businessType.isEmpty() || partnershipType.isEmpty() ||
                 yearEstablished.isEmpty() || employeesNumber.isEmpty() || turnover.isEmpty() ||
                 address.isEmpty() || country.isEmpty() || city.isEmpty() || pinCode.isEmpty() ||
                 products.isEmpty() || keywords.isEmpty() ) {


                    errors.setText("Please Enter All The Fields");

            } else {

                if(yearEstablished.length()!=4)

                    errors.setText("Please 'Year Established' must be 4 digits ");
                else {

                if (StaticMethod.ConnectChecked(context)) {

//                    saveBusiness.setEnabled(false);

                    Business business = new Business();

                    business.setBusinessName(name);
                    business.setBusinessType(businessType);
                    business.setPartnershipType(partnershipType);
                    business.setYearEstablished(yearEstablished);
                    business.setEmployeesNumber(employeesNumber);
                    business.setTurnover(turnover);
                    business.setAddress(address);
                    business.setCountry(country);
                    business.setCity(city);
                    business.setPinCode(pinCode);
                    business.setProducts(products);
                    business.setKeywords(keywords);

                    postBusinessDetails(business);

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }}

            }

        }
    };





    /**
     * Upload Business details to the server
     *
     * @param business
     */
    private void postBusinessDetails(final Business business) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final long imagename = System.currentTimeMillis();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest( Request.Method.POST, URL_SAVE_BUSINESS,
                new Response.Listener<NetworkResponse>() {

            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    JSONObject obj = new JSONObject(new String(response.data));
                    progressDialog.dismiss();

                    if(obj.getBoolean("success")) {

                        Toast.makeText(getApplicationContext(), obj.getString("message").toUpperCase(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(SaveBusinessActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                    else {

                        JSONArray message = obj.getJSONArray("message");
                        JSONObject error = message.getJSONObject(0);
                        Toast.makeText(getApplicationContext(), error.getString("year_established"), Toast.LENGTH_LONG).show();
                        errors.setText(error.getString("year_established"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        String message = "";
                        if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        //                    String responseBody = new String(volleyError.networkResponse.data, "utf-8");
//                    JSONObject jsonObject = new JSONObject(responseBody);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                    }
                }) {


            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("Content-Type", "application/json; charset=utf-8");

                params.put(BUSINESS_ID, userId);
                params.put(BUSINESS_NAME, business.getBusinessName());
                params.put(BUSINESS_TYPE, business.getBusinessType());
                params.put(BUSINESS_PARTNERSHIP_TYPE, business.getPartnershipType());
                params.put(BUSINESS_YEAR_ESTABLISHED, business.getYearEstablished());
                params.put(BUSINESS_EMPLOYESS_NUMBER, business.getEmployeesNumber());
                params.put(BUSINESS_TURNOVER, business.getTurnover());
                params.put(BUSINESS_ADDRESS, business.getAddress());
                params.put(BUSINESS_COUNTRY, business.getCountry());
                params.put(BUSINESS_CITY, business.getCity());
                params.put(BUSINESS_PIN_CODE, business.getPinCode());
                params.put(BUSINESS_PRODUCTS, business.getProducts());
                params.put(BUSINESS_KEYWORDS, business.getKeywords());

                return params;

            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                params.put("image",
                        new DataPart(
                                imagename + ".jpeg", convertBitmapToByteArray(bitmap)) );
                return params;
            }



        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.getCache().clear();
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }


}
