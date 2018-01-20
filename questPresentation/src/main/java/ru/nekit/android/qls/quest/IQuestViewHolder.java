package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.view.View;

public interface IQuestViewHolder {

    void attachQuestView(@NonNull View questView);

    void detachQuestView();

}