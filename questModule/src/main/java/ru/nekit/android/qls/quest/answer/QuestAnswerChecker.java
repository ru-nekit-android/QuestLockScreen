package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;

public class QuestAnswerChecker implements IAnswerChecker {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Object answer) {
        return quest.getAnswer().equals(answer);
    }

    @Override
    public boolean checkStringInputFormat(@NonNull IQuest quest, @NonNull String value) {
        boolean result = false;
        Class answerClass = quest.getAnswerClass();
        if (answerClass == Integer.class) {
            try {
                Integer.parseInt(value);
                result = true;
            } catch (NumberFormatException exception) {
                result = false;
            }
        } else if (answerClass == Double.class) {
            try {
                Double.parseDouble(value);
                result = true;
            } catch (NumberFormatException exception) {
                result = false;
            }
        } else if (answerClass == String.class) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean checkStringInput(@NonNull IQuest quest, @NonNull String value) {
        boolean result = false;
        Class answerClass = quest.getAnswerClass();
        if (answerClass == Integer.class) {
            result = quest.getAnswer() == Integer.valueOf(value);
        } else if (answerClass == Double.class) {
            result = quest.getAnswer() == Double.valueOf(value);
        } else if (answerClass == String.class) {
            result = ((String) quest.getAnswer()).toLowerCase().equals(value.toLowerCase());
        }
        return result;
    }
}
