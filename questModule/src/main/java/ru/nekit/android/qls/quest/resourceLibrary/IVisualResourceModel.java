package ru.nekit.android.qls.quest.resourceLibrary;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.ITitleable;

public interface IVisualResourceModel extends ITitleable {

    @Nullable
    VisualResourceGroup[] getGroups();

    @DrawableRes
    int getDrawableResourceId();

}