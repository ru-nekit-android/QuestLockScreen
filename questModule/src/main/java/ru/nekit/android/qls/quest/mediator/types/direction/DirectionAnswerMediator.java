package ru.nekit.android.qls.quest.mediator.types.direction;

import ru.nekit.android.qls.quest.mediator.answer.QuestSwipeAnswerMediator;
import ru.nekit.android.qls.utils.ViewHolder;

public class DirectionAnswerMediator extends QuestSwipeAnswerMediator {

    @Override
    protected ViewHolder getTargetViewHolder() {
        return new DirectionQuestViewHolder(mQuestContext);
    }
}