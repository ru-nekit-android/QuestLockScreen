package ru.nekit.android.qls.quest.resources.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public interface IQuestVisualRepresentationHolder {

    @Nullable
    List<Integer> getVisualRepresentationList();

    void setVisualRepresentationList(@NonNull List<Integer> value);

}
