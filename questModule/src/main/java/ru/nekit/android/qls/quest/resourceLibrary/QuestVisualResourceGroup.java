package ru.nekit.android.qls.quest.resourceLibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.ITitleable;

public enum QuestVisualResourceGroup implements ITitleable {

    CHOICE(R.string.qvrg_choice_title),
    FRUIT(R.string.qvrg_fruit_title),
    MATH_OPERATOR(R.string.qvrg_math_operator_title),
    BERRY(R.string.qvrg_berry_title, FRUIT, CHOICE),
    POMUM(R.string.qvrg_pomum_title, FRUIT, CHOICE),
    SEASONS(R.string.qvrg_seasons_title, CHOICE);

    @StringRes
    private int mTitleResourceId;
    @Nullable
    private QuestVisualResourceGroup[] mParents;

    QuestVisualResourceGroup(@StringRes int titleResourceId, @Nullable QuestVisualResourceGroup... parents) {
        mTitleResourceId = titleResourceId;
        mParents = parents;
    }

    public int getId() {
        return ordinal();
    }

    @Override
    public String getTitle(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

    @Nullable
    public QuestVisualResourceGroup[] getParents() {
        return mParents;
    }

    public List<QuestVisualResourceGroup> getChildren() {
        List<QuestVisualResourceGroup> children = new ArrayList<>();
        for (QuestVisualResourceGroup group : values()) {
            QuestVisualResourceGroup[] parents = group.getParents();
            if (parents != null) {
                for (QuestVisualResourceGroup parentItem : parents) {
                    if (parentItem == this) {
                        children.add(group);
                    }
                }
            }
        }
        return children;
    }

    public List<QuestVisualResourceItem> getQuestVisualItems() {
        List<QuestVisualResourceItem> questVisualResourceItems = new ArrayList<>();
        for (QuestVisualResourceItem questVisualResourceItem : QuestVisualResourceItem.values()) {
            if (questVisualResourceItem.getGroups() != null) {
                for (QuestVisualResourceGroup groupItem : questVisualResourceItem.getGroups()) {
                    if (groupItem.hasParent(this)) {
                        questVisualResourceItems.add(questVisualResourceItem);
                    }
                }
            }
        }
        return questVisualResourceItems;
    }

    public boolean hasParent(QuestVisualResourceGroup group) {
        boolean result = this == group;
        if (!result) {
            QuestVisualResourceGroup[] parents = getParents();
            if (parents != null) {
                for (QuestVisualResourceGroup parentItem : parents) {
                    result = parentItem.hasParent(group);
                    if (result) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}
