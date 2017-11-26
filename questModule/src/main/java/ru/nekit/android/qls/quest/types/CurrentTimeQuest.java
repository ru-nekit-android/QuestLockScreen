package ru.nekit.android.qls.quest.types;

import android.support.annotation.NonNull;

import java.util.Calendar;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.utils.MathUtils;
import ru.nekit.android.qls.utils.TimeUtils;

public class CurrentTimeQuest extends TimeQuest {

    private long timeStamp;

    public CurrentTimeQuest(@NonNull Quest quest) {
        super(quest);
        setQuestType(QuestType.CURRENT_TIME);
        setQuestionType(QuestionType.UNKNOWN_MEMBER);
        NumberSummandQuest inQuest = (NumberSummandQuest) quest;
        leftNode = inQuest.leftNode;
        int length = leftNode.length;
        unknownMemberIndex = MathUtils.randUnsignedInt(length - 1);
        timeStamp = TimeUtils.getCurrentTime();
        Calendar calendar = Calendar.getInstance();
        int currentMinutes = calendar.get(Calendar.MINUTE);
        int currentHours = calendar.get(Calendar.HOUR);
        for (int i = 0; i < length; i++) {
            if (i == unknownMemberIndex) {
                leftNode[i] = currentHours * 60 + currentMinutes;
            } else {
                int currentHoursLocal = (leftNode[i] - leftNode[i] % 60) / 60;
                while (currentHoursLocal == currentHours) {
                    currentHoursLocal = MathUtils.randUnsignedInt(12);
                }
                leftNode[i] = currentHoursLocal * 60 + currentMinutes;
            }
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Integer getAnswer() {
        return unknownMemberIndex;
    }
}