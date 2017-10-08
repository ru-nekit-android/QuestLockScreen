package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.quest.QuestContext;

public interface IQuestMediator {

    void create(@NonNull QuestContext questContext);

    void onQuestAttach(@NonNull ViewGroup rootContentContainer);

    void onQuestShow();

    void onQuestStart(boolean delayedStart);

    boolean onRightAnswer();

    void onWrongAnswer();

    void onQuestRestart();

    void onQuestPause();

    void onQuestResume();

    //call when screen is getting off
    void onQuestStop();

    void deactivate();

    void detachView();

    @Nullable
    View getView();

    void updateSize();

}