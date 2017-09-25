package ru.nekit.android.qls.quest.resourceLibrary;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.R;

import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.COLOR_AS_BACKGROUND;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.COLOR_AS_CONTENT;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.NONE;

public enum ChildrenToyQuestVisualResourceItem
        implements IColoredVisualResourceItemList {

    CAR(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_car_background, COLOR_AS_CONTENT),
            new ColoredVisualResourceItem(R.drawable.qvri_car_content, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_car_foreground, COLOR_AS_BACKGROUND))),
            R.string.qvri_car_title,
            VisualResourceGroup.BOY,
            VisualResourceGroup.CHILDREN_TOY),

    DOLL_BOOTS(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_boots, COLOR_AS_CONTENT),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_skirt, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_blouse, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_foreground, NONE))),
            R.string.qvri_doll_boots_title,
            VisualResourceGroup.GIRL,
            VisualResourceGroup.CHILDREN_TOY),

    DOLL_SKIRT(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_boots, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_skirt, COLOR_AS_CONTENT),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_blouse, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_foreground, NONE))),
            R.string.qvri_doll_skirt_title,
            VisualResourceGroup.GIRL,
            VisualResourceGroup.CHILDREN_TOY),

    DOLL_BLOUSE(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_boots, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_skirt, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_blouse, COLOR_AS_CONTENT),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_foreground, NONE))),
            R.string.qvri_doll_blouse_title,
            VisualResourceGroup.GIRL,
            VisualResourceGroup.CHILDREN_TOY);

    private final List<ColoredVisualResourceItem> mColoredVisualResourceItemList;
    @StringRes
    private int mTitleResourceId;
    @Nullable
    private VisualResourceGroup[] mGroups;

    ChildrenToyQuestVisualResourceItem(@NonNull List<ColoredVisualResourceItem> coloredVisualResourceItemList,
                                       @StringRes int titleResourceId,
                                       @Nullable VisualResourceGroup... groups) {
        mColoredVisualResourceItemList = coloredVisualResourceItemList;
        mTitleResourceId = titleResourceId;
        mGroups = groups;
    }

    int getId() {
        return ordinal();
    }

    @Override
    public String getTitle(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

    @Nullable
    public VisualResourceGroup[] getGroups() {
        return mGroups;
    }

    @NonNull
    public List<ColoredVisualResourceItem> getColoredVisualResourceItemList() {
        return mColoredVisualResourceItemList;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return 0;
    }
}