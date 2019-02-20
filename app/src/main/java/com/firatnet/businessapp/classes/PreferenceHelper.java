package com.firatnet.businessapp.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.firatnet.businessapp.entities.Register;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceHelper {


    public static final String KEY = "com.firatnet.businessapp";

    public static final String SETTING_KEY_ID = "com.firatnet.businessapp.SETTING_KEY_ID";
    public static final String SETTING_KEY_NAME = "com.firatnet.businessapp.SETTING_KEY_NAME";
    public static final String SETTING_KEY_EMAIL = "com.firatnet.businessapp.SETTING_KEY_EMAIL";
    public static final String SETTING_KEY_CREATED_AT = "com.firatnet.businessapp.SETTING_KEY_CREATED_AT";
    public static final String SETTING_KEY_UPDATED_AT = "com.firatnet.businessapp.SETTING_KEY_UPDATED_AT";
    public static final String SETTING_KEY_PASSWORD = "com.firatnet.businessapp.SETTING_KEY_PASSWORD";
    public static String SETTING_KEY_PHONE = "com.firatnet.businessapp.SETTING_KEY_PHONE";
    public static String SETTING_KEY_COUNTRY = "com.firatnet.businessapp.SETTING_KEY_COUNTRY";
    public static String SETTING_KEY_GENERATED_ID = "com.firatnet.businessapp.SETTING_KEY_GENERATED_ID";
    public static String SETTING_KEY_STATUS = "com.firatnet.businessapp.SETTING_KEY_STATUS";
    public static String SETTING_KEY_PHOTO_URL = "com.firatnet.businessapp.SETTING_KEY_PHOTO_URL";
    public static String SETTING_KEY_IP = "com.firatnet.businessapp.SETTING_KEY_IP";
    public static String SETTING_KEY_DEFAULT_FILE = "com.firatnet.businessapp.SETTING_KEY_DEFAULT_FILE";

    public static String SETTING_KEY_LOGIN_STATE = "com.firatnet.businessapp.SETTING_KEY_LOGIN_STATE";
    public static String SETTING_KEY_LOGIN_DATE = "com.firatnet.businessapp.SETTING_KEY_LOGIN_DATE";

    public static String SETTING_KEY_LAST_RUN_MP3_FILE= "com.firatnet.businessapp.SETTING_KEY_LAST_RUN_MP3_FILE";
    public static String SETTING_KEY_LAST_RUN_MP3_FILE_TITLE= "com.firatnet.businessapp.SETTING_KEY_LAST_RUN_MP3_FILE_TITLE";

    public static String SETTING_KEY_MA3_TTS= "com.firatnet.businessapp.SETTING_KEY_MA3_TTS=";
    public static String SETTING_KEY_CALL_VM= "com.firatnet.businessapp.SETTING_KEY_CALL_VM";

    public static String SETTING_VALUE_ID = "";
    public static String SETTING_VALUE_NAME = "";
    public static String SETTING_VALUE_EMAIL = "";
    public static String SETTING_VALUE_CREATED_AT = "";
    public static String SETTING_VALUE_UPDATED_AT = "";
    public static String SETTING_VALUE_PASSWORD = "";
    public static String SETTING_VALUE__PHONE = "";
    public static String SETTING_VALUE_COUNTRY = "";

    public static String SETTING_VALUE_GENERATED_ID = "";
    public static String SETTING_VALUE_STATUS = "";
    public static String SETTING_VALUE_PHOTO_URL = "";
    public static String SETTING_VALUE_IP = "";
    public static String SETTING_VALUE_DEFAULT_FILE = "";
    public static String SETTING_VALUE_LOGIN_STATE = "";
    public static String SETTING_VALUE_LOGIN_DATE = "";
    public static String SETTING_VALUE_LAST_RUN_MP3_FILE= "";
    public static String SETTING_VALUE_LAST_RUN_MP3_FILE_TITLE = "";

    public static String SETTING_VALUE_MA3_TTS = "";
    public static String SETTING_VALUE_CALL_VM= "";


    public  String getSettingValueDefaultFile() {
        return readSharedPreference(KEY, SETTING_KEY_DEFAULT_FILE);
    }

    private Context context;

    public  String getSettingValueId() {
        return readSharedPreference(KEY,SETTING_KEY_ID);
    }

    public PreferenceHelper(Context context) {
        this.context = context;
    }

    public String getSettingValueEmail() {
        return readSharedPreference(KEY, SETTING_KEY_EMAIL);
    }

    public  String getSettingValueCreatedAt() {
        return readSharedPreference(KEY,SETTING_KEY_CREATED_AT);
    }

    public  String getSettingValue_phone() {
        return readSharedPreference(KEY,SETTING_KEY_PHONE);
    }

    public  String getSettingValueCountry() {
        return readSharedPreference(KEY,SETTING_KEY_COUNTRY);
    }


    public  String getSettingValueGeneratedId() {
        return readSharedPreference(KEY,SETTING_KEY_GENERATED_ID);
    }

    public  String getSettingValueStatus() {
        return readSharedPreference(KEY,SETTING_KEY_STATUS);
    }

    public  String getSettingValuePhotoUrl() {
        return readSharedPreference(KEY,SETTING_KEY_PHOTO_URL);
    }

    public  String getSettingValueUpdatedAt() {
        return readSharedPreference(KEY,SETTING_KEY_UPDATED_AT);
    }

    public String getSettingValueName() {
        return readSharedPreference(KEY, SETTING_KEY_NAME);
    }

    public  void setSettingValuePhotoUrl(String settingValuePhotoUrl) {
        SETTING_VALUE_PHOTO_URL = settingValuePhotoUrl;
        writeSharedPreference(SETTING_VALUE_PHOTO_URL, KEY, SETTING_KEY_PHOTO_URL);
    }

    public  void setSettingValueDefaultFile(String settingValueDefaultFile) {
        SETTING_VALUE_DEFAULT_FILE = settingValueDefaultFile;

        writeSharedPreference(SETTING_VALUE_DEFAULT_FILE, KEY, SETTING_KEY_DEFAULT_FILE);

    }

    public  String getSettingKeyLastRunMp3File() {
        return readSharedPreference(KEY,SETTING_KEY_LAST_RUN_MP3_FILE);
    }

    public  void setSettingKeyLastRunMp3File(String settingKeyLastRunMp3File) {
        SETTING_KEY_LAST_RUN_MP3_FILE = settingKeyLastRunMp3File;
        writeSharedPreference(SETTING_KEY_LAST_RUN_MP3_FILE, KEY, SETTING_KEY_LAST_RUN_MP3_FILE);
    }

    public  String getSettingKeyLastRunMp3FileTitle() {
        return readSharedPreference(KEY,SETTING_KEY_LAST_RUN_MP3_FILE_TITLE);
    }

    public  void setSettingKeyLastRunMp3FileTitle(String settingKeyLastRunMp3FileTitle) {
        SETTING_KEY_LAST_RUN_MP3_FILE_TITLE = settingKeyLastRunMp3FileTitle;
        writeSharedPreference(SETTING_KEY_LAST_RUN_MP3_FILE_TITLE, KEY, SETTING_KEY_LAST_RUN_MP3_FILE_TITLE);
    }

    public  String getSettingValueMa3Tts() {
        return readSharedPreference(KEY,SETTING_KEY_MA3_TTS);
    }

    public  void setSettingValueMa3Tts(String settingValueMa3Tts) {
        SETTING_VALUE_MA3_TTS = settingValueMa3Tts;
        writeSharedPreference(SETTING_VALUE_MA3_TTS, KEY, SETTING_KEY_MA3_TTS);
    }

    public  String getSettingValueCallVm() {
        return readSharedPreference(KEY,SETTING_KEY_CALL_VM);
    }

    public  void setSettingValueCallVm(String settingValueCallVm) {
        SETTING_VALUE_CALL_VM = settingValueCallVm;
        writeSharedPreference(SETTING_VALUE_CALL_VM, KEY, SETTING_KEY_CALL_VM);

    }

    /**
     * Returns a value saved in SharedPreference
     *
     * @param key  package name of the app
     * @param name The stored value's key
     * @return Value of the key that stored in the SharedPreference
     */
    public String readSharedPreference(String key, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(key, MODE_PRIVATE);

        //0 is default_value if no vaule
        return sharedPref.getString(name, "");
    }


    /**
     * Persist a (Key, Value) pair in SharedPreference
     *
     * @param valueToSave The value to be saved
     * @param key         package name of the app
     * @param name        The stored value's key
     */
    public void writeSharedPreference(String valueToSave, String key, String name) {

        SharedPreferences sharedPref = context.getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, valueToSave);

        editor.apply();

    }


    public void setLoginState(Boolean islogin) {

        if (islogin) {
            SETTING_VALUE_LOGIN_STATE = "1";
            writeSharedPreference(SETTING_VALUE_LOGIN_STATE, KEY, SETTING_KEY_LOGIN_STATE);
        } else {
            SETTING_VALUE_LOGIN_STATE = "";
            writeSharedPreference(SETTING_VALUE_LOGIN_STATE, KEY, SETTING_KEY_LOGIN_STATE);
        }

    }

    public String getLoginState() {

        return readSharedPreference(KEY, SETTING_KEY_LOGIN_STATE);

    }

    public void deleteUser() {
        writeSharedPreference("", KEY, SETTING_KEY_ID);
        writeSharedPreference("", KEY, SETTING_KEY_NAME);
        writeSharedPreference("", KEY, SETTING_KEY_EMAIL);
        writeSharedPreference("", KEY, SETTING_KEY_CREATED_AT);
        writeSharedPreference("", KEY, SETTING_KEY_UPDATED_AT);
        writeSharedPreference("", KEY, SETTING_KEY_PASSWORD);
        writeSharedPreference("", KEY, SETTING_KEY_PHONE);
        writeSharedPreference("", KEY, SETTING_KEY_COUNTRY);
        writeSharedPreference("", KEY, SETTING_KEY_GENERATED_ID);
        writeSharedPreference("", KEY, SETTING_KEY_STATUS);
        writeSharedPreference("", KEY, SETTING_KEY_PHOTO_URL);
        writeSharedPreference("", KEY, SETTING_KEY_IP);
        writeSharedPreference("", KEY, SETTING_KEY_DEFAULT_FILE);

    }

    public void saveUser(Register register) {
        writeSharedPreference(register.getId(), KEY, SETTING_KEY_ID);
        writeSharedPreference(register.getName(), KEY, SETTING_KEY_NAME);
        writeSharedPreference(register.getEmail(), KEY, SETTING_KEY_EMAIL);
        writeSharedPreference(register.getCreated_at(), KEY, SETTING_KEY_CREATED_AT);
        writeSharedPreference(register.getUpdated_at(), KEY, SETTING_KEY_UPDATED_AT);
//        writeSharedPreference(register.getPassword(), KEY, SETTING_KEY_PASSWORD);
        writeSharedPreference(register.getPhone(), KEY, SETTING_KEY_PHONE);
        writeSharedPreference(register.getCountry(), KEY, SETTING_KEY_COUNTRY);
        writeSharedPreference(register.getGenerated_id(), KEY, SETTING_KEY_GENERATED_ID);
        writeSharedPreference(register.getStatus(), KEY, SETTING_KEY_STATUS);
        writeSharedPreference(register.getPhoto_url(), KEY, SETTING_KEY_PHOTO_URL);
        writeSharedPreference(register.getIp(), KEY, SETTING_KEY_IP);
        writeSharedPreference(register.getDefault_file(), KEY, SETTING_KEY_DEFAULT_FILE);

    }
    public void editUser(Register register) {

        writeSharedPreference(register.getName(), KEY, SETTING_KEY_NAME);
        writeSharedPreference(register.getEmail(), KEY, SETTING_KEY_EMAIL);

//        writeSharedPreference(register.getPassword(), KEY, SETTING_KEY_PASSWORD);
        writeSharedPreference(register.getPhone(), KEY, SETTING_KEY_PHONE);
       // writeSharedPreference(register.getPhoto_url(), KEY, SETTING_KEY_PHOTO_URL);

    }

}
