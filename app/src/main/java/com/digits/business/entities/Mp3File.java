package com.digits.business.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Mp3File implements Parcelable {
    private String id;
    private String name;
    private String url;
    private String user_id;
    private String created_at;
    private String updated_at;

    public Mp3File(String id, String name, String user_id, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Mp3File(String id, String name, String url, String user_id, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.user_id = user_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.user_id);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
    }

    protected Mp3File(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.url = in.readString();
        this.user_id = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
    }

    public static final Parcelable.Creator<Mp3File> CREATOR = new Parcelable.Creator<Mp3File>() {
        @Override
        public Mp3File createFromParcel(Parcel source) {
            return new Mp3File(source);
        }

        @Override
        public Mp3File[] newArray(int size) {
            return new Mp3File[size];
        }
    };
}
