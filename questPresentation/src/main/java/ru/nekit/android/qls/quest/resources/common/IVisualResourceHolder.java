package ru.nekit.android.qls.quest.resources.common;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.IStringHolder;
import ru.nekit.android.qls.quest.resources.collections.VisualResourceGroupCollection;

public interface IVisualResourceHolder extends IStringHolder {

    @Nullable
    VisualResourceGroupCollection[] getGroups();

    @DrawableRes
    int getDrawableResourceId();

}