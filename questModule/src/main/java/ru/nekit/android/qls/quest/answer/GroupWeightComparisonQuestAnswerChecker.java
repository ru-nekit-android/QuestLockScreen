package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.answer.shared.AbstractGroupWeightComparisonQuestAnswerChecker;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class GroupWeightComparisonQuestAnswerChecker
        extends AbstractGroupWeightComparisonQuestAnswerChecker {

    @Override
    protected List<Integer> getGroupList(@NonNull IQuest quest) {
        return ((NumberSummandQuest) quest).getLeftNodeAsList();
    }

}