package ru.nekit.android.qls.quest.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public interface IQuestVisualRepresentation {

    @Nullable
    List<Integer> getVisualRepresentationList();

    void setVisualRepresentationList(@NonNull List<Integer> value);

}