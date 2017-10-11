package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.quest.QuestContext;

public interface IQuestMediator {

    void onCreate(@NonNull QuestContext questContext);

    void onQuestAttach(@NonNull ViewGroup rootContentContainer);

    void onQuestStart(boolean delayedPlay);

    void onQuestPlay(boolean delayedPlay);

    boolean onRightAnswer();

    boolean onWrongAnswer();

    void onQuestReplay();

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