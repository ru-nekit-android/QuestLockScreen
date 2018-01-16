package ru.nekit.android.qls.quest.mediator.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.model.DirectionModel;

public interface ISpecialQuestAnswerMediator extends IQuestAnswerMediator {

    void onSwipe(@NonNull DirectionModel direction);

}