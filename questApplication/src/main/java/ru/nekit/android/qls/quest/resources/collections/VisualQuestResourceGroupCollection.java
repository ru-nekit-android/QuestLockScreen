package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.INameHolder;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.common.IVisualQuestResourceHolder;

public enum VisualQuestResourceGroupCollection implements INameHolder {

    CHOICE(R.string.qvrg_choice_title),
    FRUIT(R.string.qvrg_fruit_title),
    MATH_OPERATOR(R.string.qvrg_math_operator_title),
    BERRY(R.string.qvrg_berry_title, FRUIT, CHOICE),
    POMUM(R.string.qvrg_pomum_title, FRUIT, CHOICE),
    SEASONS(R.string.qvrg_seasons_title, CHOICE),
    SEX(R.string.qvrg_sex_title),
    GIRL(R.string.qvrg_girl_title, SEX),
    BOY(R.string.qvrg_boy_title, SEX),
    CHILDREN_TOY(R.string.qvrg_children_toy_title);

    @StringRes
    private int mTitleResourceId;
    @Nullable
    private VisualQuestResourceGroupCollection[] mParents;

    VisualQuestResourceGroupCollection(@StringRes int titleResourceId, @Nullable VisualQuestResourceGroupCollection... parents) {
        mTitleResourceId = titleResourceId;
        mParents = parents;
    }

    public static VisualQuestResourceGroupCollection getGroup(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }

    @NonNull
    @Override
    public String getName(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

    @Nullable
    public VisualQuestResourceGroupCollection[] getParents() {
        return mParents;
    }

    public List<VisualQuestResourceGroupCollection> getChildren() {
        List<VisualQuestResourceGroupCollection> children = new ArrayList<>();
        for (VisualQuestResourceGroupCollection group : values()) {
            VisualQuestResourceGroupCollection[] parents = group.getParents();
            if (parents != null) {
                for (VisualQuestResourceGroupCollection parentItem : parents) {
                    if (parentItem == this) {
                        children.add(group);
                    }
                }
            }
        }
        return children;
    }

    public List<IVisualQuestResourceHolder> getVisualResourceItems(@NonNull QuestResourceLibrary questResourceLibrary) {
        List<IVisualQuestResourceHolder> result = new ArrayList<>();
        for (IVisualQuestResourceHolder visualResourceModel : questResourceLibrary.getVisualQuestResourceList()) {
            if (visualResourceModel.getGroups() != null) {
                for (VisualQuestResourceGroupCollection group : visualResourceModel.getGroups()) {
                    if (group.hasParent(this)) {
                        result.add(visualResourceModel);
                    }
                }
            }
        }
        return result;
    }

    public boolean hasParent(VisualQuestResourceGroupCollection group) {
        boolean result = this == group;
        if (!result) {
            VisualQuestResourceGroupCollection[] parents = getParents();
            if (parents != null) {
                for (VisualQuestResourceGroupCollection parentItem : parents) {
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