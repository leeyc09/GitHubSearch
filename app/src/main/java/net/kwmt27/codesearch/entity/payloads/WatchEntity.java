package net.kwmt27.codesearch.entity.payloads;

import com.google.gson.annotations.SerializedName;

public class WatchEntity {

    @SerializedName("action")
    private String mAction;

    public String getAction() {
        if(mAction == null) { return ""; }
        return mAction;
    }
}
