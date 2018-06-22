package com.abhishek.abc.notes.network.models;

import com.google.gson.annotations.SerializedName;

public class UserModel extends BaseResponse {
    @SerializedName("api_key")
    String mApiKey;

    public String getApiKey() {
        return mApiKey;
    }
}
