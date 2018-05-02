package ru.nekit.android.qls.quest.view.mediator.types.trafficLight

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.pupil.avatar.PupilAvatarViewBuilder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.content.SimpleContentMediator

//ver 1.0
class TrafficLightQuestContentMediator : SimpleContentMediator() {

    private lateinit var viewHolder: TrafficLightQuestViewHolder

    override val view: View?
        get() = viewHolder.view

    override val answerInput: EditText?
        get() = null

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        viewHolder = TrafficLightQuestViewHolder(questContext).apply {
            val answer = TrafficLightResourceCollection.getById(quest.answer as Int)
            trafficRedLight.visibility = INVISIBLE
            trafficGreenLight.visibility = INVISIBLE
            if (answer == TrafficLightResourceCollection.GREEN) {
                trafficGreenLight.visibility = VISIBLE
            } else {
                trafficRedLight.visibility = VISIBLE
            }
            questContext.pupil {
                PupilAvatarViewBuilder.build(questContext, it, pupilAvatarContainer)
            }
        }
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateSizeInternal()
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        updateSizeInternal()
        super.onQuestPlay(delayedPlay)
    }

    override fun playDelayedStartAnimation() {
        //override for no start delay animation
    }

    override fun updateSize() {

    }

    override fun includeInLayout(): Boolean {
        return false
    }

    private fun updateSizeInternal() {
        val resources = questContext.resources
        val pupilAvatarView = viewHolder.pupilAvatarContainer
        val globalScale = viewHolder.trafficLightBackground.height.toFloat() / resources.getDimensionPixelSize(R.dimen.traffic_light_background_height)
        val pupilAvatarWidth = globalScale * resources.getDimensionPixelSize(R.dimen.traffic_light_avatar_pupil_width)
        val pupilAvatarScale = pupilAvatarWidth / pupilAvatarView.width
        for (i in 0 until viewHolder.pupilAvatarContainer.childCount) {
            (viewHolder.pupilAvatarContainer.getChildAt(i) as ImageView).apply {
                scaleX = pupilAvatarScale
                scaleY = pupilAvatarScale
                y = resources.getDimensionPixelSize(R.dimen.traffic_light_quest_pupil_avatar_y) * globalScale - pupilAvatarView.height * (1 + pupilAvatarScale) / 2
            }
        }
    }
}