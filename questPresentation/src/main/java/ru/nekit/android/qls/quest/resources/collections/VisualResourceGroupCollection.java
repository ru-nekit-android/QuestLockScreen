package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IStringHolder;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;

public enum VisualResourceGroupCollection implements IStringHolder {

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
    private VisualResourceGroupCollection[] mParents;

    VisualResourceGroupCollection(@StringRes int titleResourceId, @Nullable VisualResourceGroupCollection... parents) {
        mTitleResourceId = titleResourceId;
        mParents = parents;
    }

    public static VisualResourceGroupCollection getGroup(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }

    @NonNull
    @Override
    public String getString(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

    @Nullable
    public VisualResourceGroupCollection[] getParents() {
        return mParents;
    }

    public List<VisualResourceGroupCollection> getChildren() {
        List<VisualResourceGroupCollection> children = new ArrayList<>();
        for (VisualResourceGroupCollection group : values()) {
            VisualResourceGroupCollection[] parents = group.getParents();
            if (parents != null) {
                for (VisualResourceGroupCollection parentItem : parents) {
                    if (parentItem == this) {
                        children.add(group);
                    }
                }
            }
        }
        return children;
    }

    public List<IVisualResourceHolder> getVisualResourceItems(@NonNull QuestResourceLibrary questResourceLibrary) {
        List<IVisualResourceHolder> result = new ArrayList<>();
        for (IVisualResourceHolder visualResourceModel : questResourceLibrary.getVisualQuestResourceList()) {
            if (visualResourceModel.getGroups() != null) {
                for (VisualResourceGroupCollection group : visualResourceModel.getGroups()) {
                    if (group.hasParent(this)) {
                        result.add(visualResourceModel);
                    }
                }
            }
        }
        return result;
    }

    public boolean hasParent(VisualResourceGroupCollection group) {
        boolean result = this == group;
        if (!result) {
            VisualResourceGroupCollection[] parents = getParents();
            if (parents != null) {
                for (VisualResourceGroupCollection parentItem : parents) {
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