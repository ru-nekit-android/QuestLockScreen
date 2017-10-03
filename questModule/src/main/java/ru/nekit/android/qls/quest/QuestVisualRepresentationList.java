package ru.nekit.android.qls.quest;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceModel;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;

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

    public void add(@NonNull IVisualResourceModel questVisualResourceItem) {
        add(mQuestResourceLibrary.getQuestVisualResourceItemId(questVisualResourceItem));
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
