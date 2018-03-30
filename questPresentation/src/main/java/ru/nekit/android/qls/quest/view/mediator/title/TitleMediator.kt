package ru.nekit.android.qls.quest.view.mediator.title

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.qls.domain.model.quest.*
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest
import ru.nekit.android.qls.domain.useCases.ConsumeRewardUseCase
import ru.nekit.android.qls.lockScreen.mediator.LockScreenMediatorAction
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.formatter.TimeFormatter
import ru.nekit.android.qls.quest.types.PerimeterQuest
import ru.nekit.android.qls.quest.types.TextQuest
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.qls.utils.throttleClicks
import ru.nekit.android.utils.MathUtils
import ru.nekit.android.utils.ViewHolder

//ver 1.0
class TitleMediator : ITitleMediator {

    override lateinit var quest: Quest
    override lateinit var questContext: QuestContext
    private lateinit var rootContentContainer: ViewGroup
    private lateinit var viewHolder: QuestTitleViewHolder

    override var disposable: CompositeDisposable = CompositeDisposable()

    override lateinit var title: String

    override val view: View?
        get() = viewHolder.view

    private fun getString(@StringRes resId: Int): String = questContext.getString(resId)

    private fun updateTitle() {
        with(quest) {

            val questResourceRepository = questContext.questResourceRepository
            pupil { pupil ->
                if (this is NumberSummandQuest) {
                    val numberSummandQuest = this
                    when (questType) {

                        QuestType.COINS ->

                            when (questionType) {

                                QuestionType.UNKNOWN_MEMBER -> {

                                    val nominationSum = numberSummandQuest.leftNode.sumBy {
                                        CoinVisualResourceCollection.getById(it).nomination
                                    }
                                    title = java.lang.String.format(getString(R.string.quest_coins_unknown_member_title),
                                            nominationSum)
                                }

                                QuestionType.SOLUTION ->

                                    title = getString(R.string.quest_coins_solution_title)
                            }

                        QuestType.SIMPLE_EXAMPLE ->

                            when (questionType) {

                                QuestionType.UNKNOWN_MEMBER ->

                                    title = getString(R.string.quest_simple_example_unknown_member_title)

                                QuestionType.SOLUTION ->

                                    title = getString(R.string.quest_simple_example_solution_title)

                                QuestionType.COMPARISON ->

                                    title = getString(R.string.quest_simple_example_comparison_title)

                                QuestionType.UNKNOWN_OPERATION ->

                                    title = getString(R.string.quest_simple_example_unknown_operation_title)
                            }

                        QuestType.METRICS ->

                            when (questionType) {

                                QuestionType.SOLUTION ->

                                    title = getString(R.string.quest_metrics_solution_title)

                                QuestionType.COMPARISON ->

                                    title = getString(R.string.quest_metrics_comparison_title)
                            }

                        QuestType.PERIMETER -> {

                            val perimeterQuest = this as PerimeterQuest

                            when (questionType) {

                                QuestionType.SOLUTION ->


                                    title = java.lang.String.format(getString(R.string.quest_perimeter_solution_title),
                                            perimeterQuest.getFigureName(questContext))

                                QuestionType.UNKNOWN_MEMBER ->

                                    title = java.lang.String.format(getString(R.string.quest_perimeter_unknown_member_title),
                                            getString(R.string.unknown_side),
                                            perimeterQuest.perimeter)
                            }
                        }

                        QuestType.TRAFFIC_LIGHT ->

                            when (questionType) {

                                QuestionType.SOLUTION -> {

                                    val trafficLightModels = MathUtils.shuffleArray(TrafficLightResourceCollection.values())
                                    title = java.lang.String.format(getString(R.string.quest_traffic_light_solution_title),
                                            trafficLightModels[0].getRepresentation().getRandomString(questContext),
                                            trafficLightModels[1].getRepresentation().getRandomString(questContext))
                                }
                            }

                        QuestType.FRUIT_ARITHMETIC -> {

                            val fruitArithmeticQuest = this as FruitArithmeticQuest

                            when (questionType) {

                                QuestionType.SOLUTION ->

                                    title = getString(R.string.quest_fruit_arithmetic_solution_title)

                                QuestionType.COMPARISON ->

                                    title = java.lang.String.format(getString(R.string.quest_fruit_arithmetic_comparison_title),
                                            if (fruitArithmeticQuest.groupComparisonType == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT)
                                                getString(R.string.greater)
                                            else
                                                getString(R.string.less))
                            }
                        }

                        QuestType.TIME -> {

                            val timeQuest = this as TimeQuest

                            when (questionType) {

                                QuestionType.UNKNOWN_MEMBER ->

                                    title = java.lang.String.format(getString(R.string.quest_time_unknown_member_title),
                                            TimeFormatter.getTimeString(timeQuest))

                                QuestionType.COMPARISON ->

                                    title = java.lang.String.format(getString(R.string.quest_time_comparison_title),
                                            if (timeQuest.groupComparisonType == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT)
                                                getString(R.string.maximum)
                                            else
                                                getString(R.string.minimum))
                            }
                        }

                        QuestType.CURRENT_TIME ->

                            title = getString(R.string.quest_current_time_unknown_member_title)

                        QuestType.CHOICE ->

                            when (questionType) {

                                QuestionType.UNKNOWN_MEMBER -> {

                                    title = java.lang.String.format(getString(R.string.quest_choice_unknown_member_title),
                                            questResourceRepository.getNounStringRepresentation(
                                                    questResourceRepository.getVisualResourceItemById(
                                                            numberSummandQuest.unknownMember)

                                            ))
                                }

                            }

                        QuestType.MISMATCH ->

                            title = getString(R.string.quest_mismatch_unknown_member_title)

                        QuestType.CURRENT_SEASON ->

                            title = getString(R.string.quest_current_season_unknown_member_title)

                        QuestType.COLORS -> {

                            val colorQuest = quest as VisualRepresentationalNumberSummandQuest
                            val visualResourceItem = questResourceRepository.getVisualResourceItemById(
                                    colorQuest.visualRepresentationList[colorQuest.unknownMemberIndex])
                            val colorResourceItem = ColorResourceCollection.getById(colorQuest.unknownMember)
                            val formatString = "%s %s"
                            title = java.lang.String.format(getString(R.string.quest_colors_unknown_member_title),
                                    questResourceRepository.localizeAdjectiveAndNounStringResourceIfNeed(
                                            colorResourceItem,
                                            visualResourceItem,
                                            formatString))
                        }

                        QuestType.DIRECTION ->

                            title = java.lang.String.format(getString(R.string.quest_direction_unknown_member_title),
                                    DirectionResourceCollection.getById(numberSummandQuest.answer)
                                            .getRepresentation().getRandomString(questContext))
                    }
                } else if (this is TextQuest) {
                    val textQuest = this
                    title = java.lang.String.format(getString(R.string.quest_text_camouflage_title),
                            textQuest.questionStringArray!![0].length)
                }
                title = "${pupil.name}, ${title.toLowerCase()}"
                viewHolder.titleView.text = title

            }
        }
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        viewHolder = QuestTitleViewHolder(questContext)
        updateTitle()
        autoDispose {
            viewHolder.unlockKeyButton.throttleClicks {
                ConsumeRewardUseCase(questContext.repository).use(Reward.UnlockKey()) {
                    if (it) sendEvent(LockScreenMediatorAction.CLOSE)
                }
            }
        }
        listenUnlockKeyCount {
            viewHolder.unlockKeyCount.text = "$it"
        }
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        this.rootContentContainer = rootContentContainer
    }

    override fun onQuestStart(delayedPlay: Boolean) {

    }

    override fun onQuestPlay(delayedPlay: Boolean) {

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
        val unlockKeyButton: FloatingActionButton = view.findViewById(R.id.btn_unlock_key)
        val unlockKeyCount: TextView = view.findViewById(R.id.tv_unlock_key_count)
    }
}