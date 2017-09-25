package ru.nekit.android.qls.quest.types.shared;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceItem;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;

public class QuestVisualRepresentationList {

    @NonNull
    private final List<Integer> mIdsList;
    @NonNull
    private QuestResourceLibrary mQuestResourceLibrary;

    public QuestVisualRepresentationList(QuestResourceLibrary questResourceLibrary) {
        mQuestResourceLibrary = questResourceLibrary;
        mIdsList = new ArrayList<>();
    }

    public List<Integer> getIdsList() {
        return mIdsList;
    }

    public void add(@NonNull IVisualResourceItem questVisualResourceItem) {
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
