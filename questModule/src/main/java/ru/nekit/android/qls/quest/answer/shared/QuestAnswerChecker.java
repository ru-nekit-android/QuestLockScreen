package ru.nekit.android.qls.quest.answer.shared;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.base.Quest;

public class QuestAnswerChecker<T> implements IAnswerChecker<T> {

    @Override
    public boolean checkAlternativeInput(@NonNull Quest quest, @NonNull T answer) {
        return quest.getAnswer().equals(answer);
    }

    @Override
    public boolean checkStringInputFormat(@NonNull Quest quest, @NonNull String value) {
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
    public boolean checkStringInput(@NonNull Quest quest, @NonNull String value) {
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
