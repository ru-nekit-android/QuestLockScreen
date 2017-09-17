package ru.nekit.android.qls.quest.answer.shared;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.types.IGroupWeightComparisonQuest;

//TODO: SparseIntArray -> HashMap
public abstract class AbstractGroupWeightComparisonQuestAnswerChecker
        implements IAnswerChecker<Integer> {

    protected abstract List<Integer> getGroupList(@NonNull IQuest quest);

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Integer answer) {
        IGroupWeightComparisonQuest inQuest = (IGroupWeightComparisonQuest) quest;
        SparseIntArray map = new SparseIntArray();
        List<Integer> groupList = getGroupList(quest);
        final int length = groupList.size();
        int i = 0;
        for (; i < length; i++) {
            int key = groupList.get(i);
            map.append(key, map.get(key, 0) + 1);
        }
        boolean isMax = inQuest.getGroupComparisonType() == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT;
        int value = isMax ? 0 : Integer.MAX_VALUE;
        for (i = 0; i < map.size(); i++) {
            int item = map.valueAt(i);
            value = isMax ? Math.max(value, item) : Math.min(value, item);
        }
        return map.get(answer) == value;
    }

    @Override
    public boolean checkStringInputFormat(@NonNull IQuest quest, @NonNull String value) {
        return false;
    }

    @Override
    public boolean checkStringInput(@NonNull IQuest quest, @NonNull String value) {
        return false;
    }

}