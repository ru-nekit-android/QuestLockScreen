package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IQuest {

    QuestType getQuestType();

    void setQuestType(@NonNull QuestType value);

    QuestionType getQuestionType();

    void setQuestionType(@NonNull QuestionType mQuestionType);

    Object getAnswer();

    @Nullable
    Object[] getAvailableAnswerVariants();

    void setAvailableAnswerVariants(@NonNull Object[] values);

    Class getAnswerClass();

    int getAnswerInputType();

}