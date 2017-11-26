package ru.nekit.android.qls.quest.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.common.IQuestVisualRepresentation;

public class VisualRepresentationalNumberSummandQuest extends NumberSummandQuest
        implements IQuestVisualRepresentation {

    @Nullable
    List<Integer> mVisualRepresentationList;

    public VisualRepresentationalNumberSummandQuest() {
        mVisualRepresentationList = new ArrayList<>();
    }

    @Nullable
    @Override
    public List<Integer> getVisualRepresentationList() {
        return mVisualRepresentationList;
    }

    @Override
    public void setVisualRepresentationList(@NonNull List<Integer> value) {
        mVisualRepresentationList = value;
    }
}