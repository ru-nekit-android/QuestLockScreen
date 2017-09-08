package ru.nekit.android.qls.quest.types;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;

public class QuestVisualRepresentationList {

    @NonNull
    private final List<Integer> mIdsList;

    public QuestVisualRepresentationList() {
        mIdsList = new ArrayList<>();
    }

    public List<Integer> getIdsList() {
        return mIdsList;
    }

    public void add(QuestVisualResourceItem questVisualResourceItem) {
        add(questVisualResourceItem.ordinal());
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
