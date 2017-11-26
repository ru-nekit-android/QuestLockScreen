package ru.nekit.android.qls.quest.mediator.answer;

import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

public interface IButtonsQuestAnswerMediator extends IQuestAnswerMediator {

    @Nullable
    List<View> getAnswerButtonList();

}
