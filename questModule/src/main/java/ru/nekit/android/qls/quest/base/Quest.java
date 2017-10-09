package ru.nekit.android.qls.quest.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;

public abstract class Quest<AnswerType> {

    @Nullable
    protected transient Object[] mAvailableAnswerVariants;
    private QuestionType mQuestionType;
    private QuestType mQuestType;

    @NonNull
    public QuestionType getQuestionType() {
        return mQuestionType;
    }

    public void setQuestionType(@NonNull QuestionType questionType) {
        mQuestionType = questionType;
    }

    public QuestType getQuestType() {
        return mQuestType;
    }

    public void setQuestType(@NonNull QuestType questType) {
        mQuestType = questType;
    }

    @Nullable
    public Object[] getAvailableAnswerVariants() {
        return mAvailableAnswerVariants;
    }

    public void setAvailableAnswerVariants(@NonNull Object[] values) {
        mAvailableAnswerVariants = values;
    }

    public abstract AnswerType getAnswer();

    public abstract Class getAnswerClass();

    public abstract int getAnswerInputType();

}