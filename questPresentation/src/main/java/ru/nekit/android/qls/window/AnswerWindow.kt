package ru.nekit.android.qls.window

import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.view.View
import android.widget.TextView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.RecordType.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD
import ru.nekit.android.qls.domain.model.RecordType.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.window.AnswerWindowType.RIGHT
import ru.nekit.android.qls.window.AnswerWindowType.WRONG
import ru.nekit.android.qls.window.common.ContentWithToolQuestWindow
import ru.nekit.android.utils.ViewHolder
import java.text.SimpleDateFormat
import java.util.*

private object AnswerWindow {

    fun open(questContext: QuestContext,
             type: AnswerWindowType,
             @StyleRes styleResId: Int,
             @LayoutRes contentResId: Int,
             @LayoutRes toolContentResId: Int) {
        ContentWithToolQuestWindow.Builder(questContext, type.name)
                .setStyle(styleResId)
                .setContent(contentResId)
                .setToolContent(toolContentResId)
                .create()
                .open()
    }

    fun open(questContext: QuestContext,
             type: AnswerWindowType,
             @StyleRes styleResId: Int,
             content: ViewHolder,
             @LayoutRes toolContentResId: Int) {
        ContentWithToolQuestWindow.Builder(questContext, type.name)
                .setStyle(styleResId)
                .setContent(content)
                .setToolContent(toolContentResId)
                .create()
                .open()
    }

}

enum class AnswerWindowType {

    RIGHT,
    WRONG;

}

object WrongAnswerWindow {

    fun open(questContext: QuestContext,
             @StyleRes styleResId: Int,
             @LayoutRes contentResId: Int,
             @LayoutRes toolContentResId: Int) {
        AnswerWindow.open(questContext, WRONG, styleResId, contentResId, toolContentResId)
    }

    fun openSimple(questContext: QuestContext) {
        AnswerWindow.open(questContext, WRONG,
                R.style.Window_WrongAnswer_Simple,
                ViewHolder(questContext, R.layout.wc_wrong_answer_simple_content).apply {
                    buildTitle(questContext, view, AnswerType.WRONG)
                },
                R.layout.wc_wrong_answer_tool_simple_content)
    }

    fun open(questContext: QuestContext,
             @StyleRes styleResId: Int,
             content: ViewHolder,
             @LayoutRes toolContentResId: Int) {
        AnswerWindow.open(questContext, WRONG, styleResId, content, toolContentResId)
    }

    fun openDefault(questContext: QuestContext) {
        WrongAnswerWindow.open(
                questContext,
                R.style.Window_WrongAnswer,
                ViewHolder(questContext, R.layout.wc_wrong_answer_content).apply {
                    buildTitle(questContext, view, AnswerType.WRONG)
                },
                R.layout.wc_wrong_answer_tool_simple_content
        )
    }
}

private fun buildTitle(questContext: QuestContext, view: View, answerType: AnswerType) {
    (view.findViewById(R.id.tv_title) as TextView).apply {
        text = answerType.getRepresentation().getRandomString(questContext)
    }
}


object RightAnswerWindow {

    fun open(questContext: QuestContext,
             @StyleRes styleResId: Int,
             @LayoutRes contentResId: Int,
             @LayoutRes toolContentResId: Int) {
        AnswerWindow.open(questContext, RIGHT, styleResId, contentResId, toolContentResId)
    }

    fun openSimple(questContext: QuestContext) {
        AnswerWindow.open(questContext, RIGHT,
                R.style.Window_RightAnswer_Simple,
                ViewHolder(questContext, R.layout.wc_right_answer_simple_content).apply {
                    buildTitle(questContext, view, AnswerType.RIGHT)
                },
                R.layout.wc_right_answer_tool_simple_content)
    }

    fun open(questContext: QuestContext,
             @StyleRes styleResId: Int,
             content: ViewHolder,
             @LayoutRes toolContentResId: Int) {
        AnswerWindow.open(questContext, RIGHT, styleResId, content, toolContentResId)
    }

    fun openDefault(questContext: QuestContext) {
        questContext.apply {
            pupil { pupil ->
                questHistory { questHistory ->
                    questPreviousHistoryWithBestSessionTime(questHistory!!) { previousHistory ->
                        questStatisticsReport { report ->
                            RightAnswerWindow.open(
                                    this,
                                    R.style.Window_RightAnswer,
                                    ViewHolder(this, R.layout.wc_right_answer_content).apply {
                                        val dateFormat = SimpleDateFormat(
                                                getString(R.string.right_answer_timer_formatter),
                                                Locale.getDefault())
                                        ArrayList<String>().let { list ->
                                            list.add("${pupil.name}, ${
                                            AnswerType.RIGHT.getRepresentation().getRandomString(questContext).toLowerCase()
                                            }")
                                            list.add(String.format(questContext.getString(R.string.right_answer_time_formatter),
                                                    dateFormat.format(questHistory.sessionTime)))
                                            if (previousHistory != null && (questHistory.recordTypes and RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD.value) != 0)
                                                list.add(String.format(questContext.getString(R.string.right_answer_best_time_update_formatter),
                                                        dateFormat.format(previousHistory.sessionTime)))
                                            if ((questHistory.recordTypes and RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD.value) != 0)
                                                list.add(String.format(getString(R.string.right_answer_series_length_update_formatter),
                                                        report.rightAnswerSeriesCount))
                                            if (questHistory.rewards.isNotEmpty()) {
                                                list.add("Награды: ${questHistory.rewards.size}")
                                                questHistory.rewards.forEach {
                                                    list.add(it.getRepresentation().getString(questContext))
                                                }
                                            }
                                            (view.findViewById(R.id.tv_title) as TextView).apply {
                                                text = list.joinToString("\n")
                                            }
                                        }
                                    },
                                    R.layout.wc_right_answer_tool_simple_content
                            )
                        }
                    }
                }
            }
        }
    }
}