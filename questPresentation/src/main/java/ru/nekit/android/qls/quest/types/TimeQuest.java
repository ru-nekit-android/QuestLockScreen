package ru.nekit.android.qls.quest.types;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resources.common.IGroupWeightComparisonQuest;
import ru.nekit.android.qls.utils.MathUtils;

public class TimeQuest extends NumberSummandQuest implements IGroupWeightComparisonQuest {

    private int groupComparisonType;

    public TimeQuest(@NonNull Quest quest) {
        setQuestType(QuestType.TIME);
        setQuestionType(quest.getQuestionType());
        NumberSummandQuest inQuest = (NumberSummandQuest) quest;
        leftNode = inQuest.leftNode;
        int length = leftNode.length;
        if (quest.getQuestionType() == QuestionType.UNKNOWN_MEMBER) {
            unknownMemberIndex = MathUtils.randUnsignedInt(length - 1);
        }
        if (quest.getQuestionType() == QuestionType.COMPARISON) {
            groupComparisonType = MathUtils.randInt(MIN_GROUP_WEIGHT, MAX_GROUP_WEIGHT);
        }
    }

    public static int getTimeHours(int time) {
        return (time - time % 60) / 60;
    }

    public static int getTimeMinutes(int time) {
        return time - getTimeHours(time) * 60;
    }

    private int getHoursByIndex(int index) {
        int time = leftNode[index];
        return (time - time % 60) / 60;
    }

    private int getMinutesByIndex(int index) {
        int time = leftNode[index];
        return time - getHoursByIndex(index) * 60;
    }

    public int getUnknownTime() {
        return getUnknownMember();
    }

    public int getUnknownTimeHours() {
        return getHoursByIndex(unknownMemberIndex);
    }

    public int getUnknownTimeMinutes() {
        return getMinutesByIndex(unknownMemberIndex);
    }

    private String getTimeString(int index) {
        int hours = getHoursByIndex(index);
        int minutes = getMinutesByIndex(index);
        return String.format("%s:%s", hours, minutes < 10 ? "0" + minutes : minutes);
    }

    public String getUnknownTimeString() {
        return getTimeString(unknownMemberIndex);
    }

    @Override
    public int getGroupComparisonType() {
        return getQuestionType() == QuestionType.COMPARISON ? groupComparisonType : -1;
    }

    @Override
    public Integer getAnswer() {
        if (getQuestionType() == QuestionType.COMPARISON) {
            boolean isMax = getGroupComparisonType() == MAX_GROUP_WEIGHT;
            int answer = isMax ? 0 : Integer.MAX_VALUE;
            for (int item : leftNode) {
                answer = isMax ? Math.max(answer, item) : Math.min(answer, item);
            }
            return answer;
        }
        return super.getAnswer();
    }
}