package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.nekit.android.qls.quest.QuestContext;

interface IQuestMediator {

    void init(@NonNull QuestContext questContext);

    void destroy();

    boolean isDestroyed();

    @Nullable
    View getView();

    void updateSize(int width, int height);

    void playAnimationOnDelayedStart(int duration, @Nullable View view);
}
