package ru.nekit.android.qls.quest.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;

public abstract class AbstractQuest implements IQuest {

    @Nullable
    protected transient Object[] mAvailableAnswerVariants;
    private QuestionType mQuestionType;
    private QuestType mQuestType;

    @Override
    @NonNull
    public QuestionType getQuestionType() {
        return mQuestionType;
    }

    @Override
    public void setQuestionType(@NonNull QuestionType questionType) {
        mQuestionType = questionType;
    }

    @Override
    public QuestType getQuestType() {
        return mQuestType;
    }

    @Override
    public void setQuestType(@NonNull QuestType questType) {
        mQuestType = questType;
    }

    @Nullable
    @Override
    public Object[] getAvailableAnswerVariants() {
        return mAvailableAnswerVariants;
    }

    @Override
    public void setAvailableAnswerVariants(@NonNull Object[] values) {
        mAvailableAnswerVariants = values;
    }

}