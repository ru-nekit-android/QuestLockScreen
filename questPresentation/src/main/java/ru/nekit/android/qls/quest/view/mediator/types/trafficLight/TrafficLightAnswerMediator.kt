package ru.nekit.android.qls.quest.view.mediator.types.trafficLight

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.answer.TrafficLightQuestAnswerVariantAdapter
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.utils.MathUtils

//ver 1.0
class TrafficLightAnswerMediator : ButtonListAnswerMediator(TrafficLightQuestAnswerVariantAdapter()) {

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        when (quest.questionType) {

            QuestionType.SOLUTION ->

                fillButtonListWithAvailableVariants(true)

            else -> {
            }
        }
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        if (answerType == AnswerType.WRONG) {
            refillButtonListWithAvailableVariants(true)
        }
        return super.onAnswer(answerType)
    }

    override fun createButton(label: String, tag: Any,
                              layoutParams: LinearLayout.LayoutParams,
                              isFirst: Boolean,
                              isLast: Boolean): View {
        val button = LayoutInflater.from(questContext).inflate(R.layout.button_traffic_light, null) as TextView
        val buttonBackgrounds = intArrayOf(R.drawable.background_button_white, R.drawable.background_button_red, R.drawable.background_button_green)
        val trafficLightModel = TrafficLightResourceCollection.getById(tag as Int)
        var buttonBackground: Int
        do {
            buttonBackground = MathUtils.randItem(buttonBackgrounds)
        } while (trafficLightModel == TrafficLightResourceCollection.GREEN &&
                buttonBackground == R.drawable.background_button_red ||
                trafficLightModel == TrafficLightResourceCollection.RED &&
                buttonBackground == R.drawable.background_button_green)
        button.setBackgroundResource(buttonBackground)
        val margin = questContext.resources.getDimensionPixelSize(R.dimen.normal_gap)
        layoutParams.setMargins(margin, 0, margin, 0)
        return button
    }
}