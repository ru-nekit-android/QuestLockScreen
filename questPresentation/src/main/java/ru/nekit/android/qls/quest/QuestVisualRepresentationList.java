package ru.nekit.android.qls.quest;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;

public class QuestVisualRepresentationList {

    @NonNull
    private final List<Integer> mIdsList;
    @NonNull
    private QuestResourceLibrary mQuestResourceLibrary;

    public QuestVisualRepresentationList(@NonNull QuestResourceLibrary questResourceLibrary) {
        mQuestResourceLibrary = questResourceLibrary;
        mIdsList = new ArrayList<>();
    }

    public List<Integer> getIdsList() {
        return mIdsList;
    }

    public void add(@NonNull IVisualResourceHolder questVisualResourceItem) {
        add(mQuestResourceLibrary.getQuestVisualResourceId(questVisualResourceItem));
    }

    public void add(int id) {
        mIdsList.add(id);
    }

    public int size() {
        return mIdsList.size();
    }

    public int get(int position) {
        return mIdsList.get(position);
    }
}
