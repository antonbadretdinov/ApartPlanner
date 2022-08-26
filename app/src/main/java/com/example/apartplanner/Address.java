package com.example.apartplanner;

import com.example.apartplanner.model.Studio;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Address {
    private String mKey;
    private String mName;
    private String mImageUrl;
    private ArrayList<Studio> studioList;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public void setStudioList(ArrayList<Studio> studioList) {
        this.studioList = studioList;
    }

    public ArrayList<Studio> getStudioList() {
        return studioList;
    }

    public Address() {
    }

    public Address(String name, String imageUrl, ArrayList<Studio> studioList) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        this.studioList = studioList;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }


}
