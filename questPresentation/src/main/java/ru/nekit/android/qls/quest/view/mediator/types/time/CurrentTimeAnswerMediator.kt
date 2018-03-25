package ru.nekit.android.qls.quest.view.mediator.types.time

import ru.nekit.android.qls.domain.model.quest.CurrentTimeQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext

//ver 1.0
class CurrentTimeAnswerMediator : TimeAnswerMediator() {

    private lateinit var mQuest: CurrentTimeQuest

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        mQuest = quest as CurrentTimeQuest
        super.onCreate(questContext, quest)
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        super.onQuestPlay(delayedPlay)
        listenSessionTime {
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onQuestReplay() {
        super.onQuestReplay()
        listenSessionTime {
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun createListAdapter(listData: List<Int>): CurrentTimeAdapter {
        return CurrentTimeAdapter(mQuest, listData, answerPublisher)
    }

}