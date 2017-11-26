package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import java.util.List;

import ru.nekit.android.qls.quest.answer.common.AbstractGroupWeightComparisonQuestAnswerChecker;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class GroupWeightComparisonQuestAnswerChecker
        extends AbstractGroupWeightComparisonQuestAnswerChecker<Integer> {

    @Override
    protected List<Integer> getGroupList(@NonNull Quest quest) {
        return ((NumberSummandQuest) quest).getLeftNodeAsList();
    }

}