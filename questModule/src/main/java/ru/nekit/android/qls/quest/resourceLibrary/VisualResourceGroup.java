package ru.nekit.android.qls.quest.resourceLibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.ITitleable;

public enum VisualResourceGroup implements ITitleable {

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
    private VisualResourceGroup[] mParents;

    VisualResourceGroup(@StringRes int titleResourceId, @Nullable VisualResourceGroup... parents) {
        mTitleResourceId = titleResourceId;
        mParents = parents;
    }

    public static VisualResourceGroup getGroup(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }

    @Override
    public String getTitle(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

    @Nullable
    public VisualResourceGroup[] getParents() {
        return mParents;
    }

    public List<VisualResourceGroup> getChildren() {
        List<VisualResourceGroup> children = new ArrayList<>();
        for (VisualResourceGroup group : values()) {
            VisualResourceGroup[] parents = group.getParents();
            if (parents != null) {
                for (VisualResourceGroup parentItem : parents) {
                    if (parentItem == this) {
                        children.add(group);
                    }
                }
            }
        }
        return children;
    }

    public List<IVisualResourceModel> getVisualResourceItems(@NonNull QuestResourceLibrary questResourceLibrary) {
        List<IVisualResourceModel> result = new ArrayList<>();
        for (IVisualResourceModel visualResourceModel : questResourceLibrary.getVisualResourceItems()) {
            if (visualResourceModel.getGroups() != null) {
                for (VisualResourceGroup group : visualResourceModel.getGroups()) {
                    if (group.hasParent(this)) {
                        result.add(visualResourceModel);
                    }
                }
            }
        }
        return result;
    }

    public boolean hasParent(VisualResourceGroup group) {
        boolean result = this == group;
        if (!result) {
            VisualResourceGroup[] parents = getParents();
            if (parents != null) {
                for (VisualResourceGroup parentItem : parents) {
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