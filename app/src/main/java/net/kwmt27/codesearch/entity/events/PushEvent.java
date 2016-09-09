package net.kwmt27.codesearch.entity.events;

import com.google.gson.annotations.SerializedName;

import net.kwmt27.codesearch.entity.EventEntity;
import net.kwmt27.codesearch.entity.payloads.PushEntity;

public class PushEvent extends EventEntity {

    @SerializedName("payload")
    private PushEntity mPushEntity;

}
