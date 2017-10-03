package ru.nekit.android.qls.quest.mediator.types.trafficLight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

/**
 * Created by nekit on 17.01.17.
 */

class TrafficLightQuestViewHolder extends ViewHolder {

    View trafficGreenLight, trafficRedLight, trafficLightBackground;
    ViewGroup pupilAvatarContainer;

    TrafficLightQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_traffic_light);
        pupilAvatarContainer = (ViewGroup) getView().findViewById(R.id.container_pupil_avatar);
        trafficGreenLight = getView().findViewById(R.id.view_traffic_green_light);
        trafficLightBackground = getView().findViewById(R.id.view_traffic_light_background);
        trafficRedLight = getView().findViewById(R.id.view_traffic_red_light);
    }

}
