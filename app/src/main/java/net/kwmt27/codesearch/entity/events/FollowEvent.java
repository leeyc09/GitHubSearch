package net.kwmt27.codesearch.entity.events;

import com.google.gson.annotations.SerializedName;

import net.kwmt27.codesearch.entity.EventEntity;
import net.kwmt27.codesearch.entity.payloads.FollowEntity;

public class FollowEvent extends EventEntity {

    @SerializedName("payload")
    private FollowEntity mFollowEntity;

//    @Override
//    public void action(TextView view, ClickableSpan clickableSpan) {
//        String repoName = getRepo().getName();
//        String action = this.getClass().getSimpleName().replace("Event", "").toLowerCase() + "ed " + repoName; // TODO
//        view.setText(action);
//        TextViewUtil.addLink(view, repoName, clickableSpan);
//    }

}
