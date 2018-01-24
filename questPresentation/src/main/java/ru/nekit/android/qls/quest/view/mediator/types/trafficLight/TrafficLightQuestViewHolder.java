package ru.nekit.android.qls.quest.view.mediator.types.trafficLight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class TrafficLightQuestViewHolder extends ViewHolder {

    View trafficGreenLight, trafficRedLight, trafficLightBackground;
    ViewGroup pupilAvatarContainer;

    TrafficLightQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_traffic_light);
        pupilAvatarContainer = (ViewGroup) view.findViewById(R.id.container_pupil_avatar);
        trafficGreenLight = view.findViewById(R.id.view_traffic_green_light);
        trafficLightBackground = view.findViewById(R.id.view_traffic_light_background);
        trafficRedLight = view.findViewById(R.id.view_traffic_red_light);
    }

}
