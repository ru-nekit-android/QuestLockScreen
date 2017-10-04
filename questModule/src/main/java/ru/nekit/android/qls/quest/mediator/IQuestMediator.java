package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.quest.QuestContext;

public interface IQuestMediator {

    void activate(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer);

    void onCreateQuest();

    void onStartQuest(boolean delayedStart);

    boolean onRightAnswer();

    void onWrongAnswer();

    void onRestartQuest();

    void onPauseQuest();

    void onResumeQuest();

    //call when screen is getting off
    void onStopQuest();

    void deactivate();

    void detachView();

    @Nullable
    View getView();

    void updateSize();

}