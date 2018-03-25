package ru.nekit.android.qls.quest.view.mediator.types.trafficLight

import android.content.Context
import android.view.View
import android.view.ViewGroup

import ru.nekit.android.qls.R
import ru.nekit.android.qls.utils.ViewHolder

//ver 1.0
internal class TrafficLightQuestViewHolder(context: Context) : ViewHolder(context, R.layout.ql_traffic_light) {

    var trafficGreenLight: View = view.findViewById(R.id.view_traffic_green_light)
    var trafficRedLight: View = view.findViewById(R.id.view_traffic_red_light)
    var trafficLightBackground: View = view.findViewById(R.id.view_traffic_light_background)
    var pupilAvatarContainer: ViewGroup = view.findViewById(R.id.container_pupil_avatar) as ViewGroup

}
