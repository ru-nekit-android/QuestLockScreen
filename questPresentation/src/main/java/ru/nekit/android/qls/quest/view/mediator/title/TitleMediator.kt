package ru.nekit.android.qls.quest.view.mediator.title

import android.content.Context
import android.support.annotation.CallSuper
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.string.*
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.QuestState
import ru.nekit.android.qls.domain.model.quest.*
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent.QUEST_PLAY
import ru.nekit.android.qls.quest.formatter.TimeFormatter
import ru.nekit.android.qls.quest.types.PerimeterQuest
import ru.nekit.android.qls.quest.types.TextQuest
import ru.nekit.android.qls.shared.model.QuestType.*
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.utils.MathUtils
import ru.nekit.android.utils.ViewHolder

//ver 1.1
class TitleMediator : ITitleMediator {

    override lateinit var quest: Quest
    override lateinit var questContext: QuestContext
    private lateinit var rootContentContainer: ViewGroup
    private lateinit var viewHolder: QuestTitleViewHolder

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    override lateinit var title: String

    override val view: View
        get() = viewHolder.view

    private fun updateTitle() {
        with(quest) {
            val questResourceRepository = questContext.questResourceRepository
            if (this is NumberSummandQuest) {
                val numberSummandQuest = this
                when (questType) {

                    COINS ->

                        when (questionType) {

                            QuestionType.UNKNOWN_MEMBER -> {

                                val nominationSum = numberSummandQuest.leftNode.sumBy {
                                    CoinVisualResourceCollection.getById(it).nomination
                                }
                                title = java.lang.String.format(getString(quest_coins_unknown_member_title),
                                        nominationSum)
                            }

                            QuestionType.SOLUTION ->

                                title = getString(quest_coins_solution_title)
                        }

                    SIMPLE_EXAMPLE ->

                        when (questionType) {

                            QuestionType.UNKNOWN_MEMBER ->

                                title = getString(quest_simple_example_unknown_member_title)

                            QuestionType.SOLUTION ->

                                title = getString(quest_simple_example_solution_title)

                            QuestionType.COMPARISON ->

                                title = getString(quest_simple_example_comparison_title)

                            QuestionType.UNKNOWN_OPERATION ->

                                title = getString(quest_simple_example_unknown_operation_title)
                        }

                    METRICS ->

                        when (questionType) {

                            QuestionType.SOLUTION ->

                                title = getString(quest_metrics_solution_title)

                            QuestionType.COMPARISON ->

                                title = getString(quest_metrics_comparison_title)
                        }

                    PERIMETER -> {

                        val perimeterQuest = this as PerimeterQuest

                        when (questionType) {

                            QuestionType.SOLUTION ->


                                title = java.lang.String.format(getString(quest_perimeter_solution_title),
                                        perimeterQuest.getFigureName(questContext))

                            QuestionType.UNKNOWN_MEMBER ->

                                title = java.lang.String.format(getString(quest_perimeter_unknown_member_title),
                                        getString(unknown_side),
                                        perimeterQuest.perimeter)
                        }
                    }

                    TRAFFIC_LIGHT ->

                        when (questionType) {

                            QuestionType.SOLUTION -> {

                                val trafficLightModels = MathUtils.shuffleArray(TrafficLightResourceCollection.values())
                                title = java.lang.String.format(getString(quest_traffic_light_solution_title),
                                        trafficLightModels[0].getRepresentation().getRandomString(questContext),
                                        trafficLightModels[1].getRepresentation().getRandomString(questContext))
                            }
                        }

                    FRUIT_ARITHMETIC -> {

                        val fruitArithmeticQuest = this as FruitArithmeticQuest

                        when (questionType) {

                            QuestionType.SOLUTION ->

                                title = getString(quest_fruit_arithmetic_solution_title)

                            QuestionType.COMPARISON ->

                                title = java.lang.String.format(getString(quest_fruit_arithmetic_comparison_title),
                                        if (fruitArithmeticQuest.groupComparisonType == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT)
                                            getString(greater)
                                        else
                                            getString(less))
                        }
                    }

                    TIME -> {

                        val timeQuest = this as TimeQuest

                        when (questionType) {

                            QuestionType.UNKNOWN_MEMBER ->

                                title = java.lang.String.format(getString(quest_time_unknown_member_title),
                                        TimeFormatter.getTimeString(timeQuest))

                            QuestionType.COMPARISON ->

                                title = java.lang.String.format(getString(quest_time_comparison_title),
                                        if (timeQuest.groupComparisonType == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT)
                                            getString(maximum)
                                        else
                                            getString(minimum))
                        }
                    }

                    CURRENT_TIME ->

                        title = getString(quest_current_time_unknown_member_title)

                    CHOICE ->

                        when (questionType) {

                            QuestionType.UNKNOWN_MEMBER -> {

                                title = java.lang.String.format(getString(quest_choice_unknown_member_title),
                                        questResourceRepository.getNounStringRepresentation(
                                                questResourceRepository.getVisualResourceItemById(
                                                        numberSummandQuest.unknownMember)

                                        ))
                            }

                        }

                    MISMATCH ->

                        title = getString(quest_mismatch_unknown_member_title)

                    CURRENT_SEASON ->

                        title = getString(quest_current_season_unknown_member_title)

                    COLORS -> {

                        val colorQuest = quest as VisualRepresentationalNumberSummandQuest
                        val visualResourceItem = questResourceRepository.getVisualResourceItemById(
                                colorQuest.visualRepresentationList[colorQuest.unknownMemberIndex])
                        val colorResourceItem = ColorResourceCollection.getById(colorQuest.unknownMember)
                        val formatString = "%s %s"
                        title = java.lang.String.format(getString(quest_colors_unknown_member_title),
                                questResourceRepository.localizeAdjectiveAndNounStringResourceIfNeed(
                                        colorResourceItem,
                                        visualResourceItem,
                                        formatString))
                    }

                    DIRECTION ->

                        title = java.lang.String.format(getString(quest_direction_unknown_member_title),
                                DirectionResourceCollection.getById(numberSummandQuest.answer)
                                        .getRepresentation().getRandomString(questContext))
                }
            } else if (this is TextQuest) {
                val textQuest = this
                title = java.lang.String.format(getString(quest_text_camouflage_title),
                        textQuest.questionStringArray!![0].length)
            }
            viewHolder.titleView.text = title.toLowerCase()
        }
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        viewHolder = QuestTitleViewHolder(questContext)
        listenForQuestEvent {
            when (it) {
                QUEST_PLAY -> view.visibility = VISIBLE
            }
        }
        view.visibility = INVISIBLE
        questHasState(QuestState.DELAYED_PLAY) {
            view.visibility = if (it)
                INVISIBLE
            else
                VISIBLE
        }
        updateTitle()
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        this.rootContentContainer = rootContentContainer
        updateTitle()
    }

    override fun onQuestStart(delayedPlay: Boolean) {
        updateTitle()
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        updateTitle()
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        if (answerType == AnswerType.WRONG) {
            updateTitle()
        }
        return true
    }

    override fun onQuestPause() {

    }

    override fun onQuestResume() {

    }

    override fun onQuestStop() {

    }

    override fun deactivate() {
        dispose()
    }

    override fun onQuestReplay() {

    }

    @CallSuper
    override fun detachView() {
    }

    override fun updateSize() {}


    internal class QuestTitleViewHolder(context: Context) : ViewHolder(context, R.layout.layout_quest_title) {

        val titleView: TextView = view.findViewById(R.id.tv_title) as TextView

    }
}