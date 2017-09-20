package ru.nekit.android.qls.quest.types.shared;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IQuestVisualRepresentation {

    @Nullable
    QuestVisualRepresentationList getVisualRepresentationList();

    void setVisualRepresentationList(@NonNull QuestVisualRepresentationList value);

}
