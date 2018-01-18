package ru.nekit.android.qls.quest.resources.common;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.INameHolder;
import ru.nekit.android.qls.quest.resources.collections.VisualQuestResourceGroupCollection;

public interface IVisualQuestResourceHolder extends INameHolder {

    @Nullable
    VisualQuestResourceGroupCollection[] getGroups();

    @DrawableRes
    int getDrawableResourceId();

}