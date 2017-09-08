package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.view.View;

public interface IQuestViewHolder {

    void attachQuestContent(@NonNull View questView);

    void detachQuestView();

}