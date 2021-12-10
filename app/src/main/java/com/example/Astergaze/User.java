package com.example.Astergaze;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String fName, profileUrl, uuid, token;
    private boolean online;

    public User() {
    }

    public User(String fName, String profileUrl, String uuid) {
        this.fName = fName;
        this.profileUrl = profileUrl;
        this.uuid = uuid;
    }

    protected User(Parcel in) {
        fName = in.readString();
        profileUrl = in.readString();
        uuid = in.readString();
        token = in.readString();
        online = in.readInt() == 1;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in)  {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFName() {
        return fName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getToken() {
        return token;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(fName);
        parcel.writeString(profileUrl);
        parcel.writeString(uuid);
        parcel.writeString(token);
        parcel.writeInt(online ? 1 : 0);
    }
}
