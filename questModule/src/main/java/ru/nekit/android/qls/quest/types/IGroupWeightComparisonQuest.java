package ru.nekit.android.qls.quest.types;

import ru.nekit.android.qls.quest.IQuest;

public interface IGroupWeightComparisonQuest extends IQuest {

    int MIN_GROUP_WEIGHT = 0;
    int MAX_GROUP_WEIGHT = 1;

    int getGroupComparisonType();
}
