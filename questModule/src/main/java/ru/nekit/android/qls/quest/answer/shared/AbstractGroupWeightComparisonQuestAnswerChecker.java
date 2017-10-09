package ru.nekit.android.qls.quest.answer.shared;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.nekit.android.qls.quest.base.IGroupWeightComparisonQuest;
import ru.nekit.android.qls.quest.base.Quest;

public abstract class AbstractGroupWeightComparisonQuestAnswerChecker<T>
        implements IAnswerChecker<T> {

    protected abstract List<T> getGroupList(@NonNull Quest quest);

    @Override
    public boolean checkAlternativeInput(@NonNull Quest quest, @NonNull T answer) {
        IGroupWeightComparisonQuest inQuest = (IGroupWeightComparisonQuest) quest;
        HashMap<T, Integer> map = new HashMap<>();
        List<T> groupList = getGroupList(quest);
        final int length = groupList.size();
        int i = 0;
        for (; i < length; i++) {
            T key = groupList.get(i);
            Integer value = map.get(key);
            value = value == null ? 0 : value;
            map.put(key, value + 1);
        }
        boolean isMax = inQuest.getGroupComparisonType() == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT;
        int value = isMax ? 0 : Integer.MAX_VALUE;
        for (Map.Entry<T, Integer> entryItem : map.entrySet()) {
            int item = entryItem.getValue();
            value = isMax ? Math.max(value, item) : Math.min(value, item);
        }
        return map.get(answer) == value;
    }

    @Override
    public boolean checkStringInputFormat(@NonNull Quest quest, @NonNull String value) {
        return false;
    }

    @Override
    public boolean checkStringInput(@NonNull Quest quest, @NonNull String value) {
        return false;
    }

}