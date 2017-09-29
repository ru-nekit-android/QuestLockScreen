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

import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.AS_PRIMARY;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.AS_SECONDARY;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.INVERSE_AS_PRIMARY;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.INVERSE_AS_SECONDARY;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.NONE;

public enum ChildrenToyVisualResourceModel
        implements IColoredVisualResourceModelList {

    CAR(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_car_background, AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_car_content, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_car_foreground, AS_SECONDARY))),
            R.drawable.qvri_car,
            R.string.qvri_car_title,
            VisualResourceGroup.BOY,
            VisualResourceGroup.CHILDREN_TOY),

    DOLL_BOOTS(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_boots, AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_skirt, INVERSE_AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_blouse, INVERSE_AS_SECONDARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_foreground, NONE))),
            R.drawable.qvri_girl,
            R.string.qvri_doll_boots_title,
            VisualResourceGroup.GIRL,
            VisualResourceGroup.CHILDREN_TOY),

    DOLL_SKIRT(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_boots, INVERSE_AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_skirt, AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_blouse, INVERSE_AS_SECONDARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_foreground, NONE))),
            R.drawable.qvri_girl,
            R.string.qvri_doll_skirt_title,
            VisualResourceGroup.GIRL,
            VisualResourceGroup.CHILDREN_TOY),

    DOLL_BLOUSE(new ArrayList<>(Arrays.asList(
            new ColoredVisualResourceItem(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_boots, INVERSE_AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_skirt, INVERSE_AS_SECONDARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_blouse, AS_PRIMARY),
            new ColoredVisualResourceItem(R.drawable.qvri_doll_foreground, NONE))),
            R.drawable.qvri_girl,
            R.string.qvri_doll_blouse_title,
            VisualResourceGroup.GIRL,
            VisualResourceGroup.CHILDREN_TOY);

    private final List<ColoredVisualResourceItem> mColoredVisualResourceItemList;
    @DrawableRes
    private int mSourceDrawableResourceId;
    @StringRes
    private int mTitleResourceId;
    @Nullable
    private VisualResourceGroup[] mGroups;

    ChildrenToyVisualResourceModel(@NonNull List<ColoredVisualResourceItem> coloredVisualResourceItemList,
                                   @DrawableRes int sourceDrawableResourceId,
                                   @StringRes int titleResourceId,
                                   @Nullable VisualResourceGroup... groups) {
        mColoredVisualResourceItemList = coloredVisualResourceItemList;
        mSourceDrawableResourceId = sourceDrawableResourceId;
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
    public List<ColoredVisualResourceItem> getColoredVisualResourceModelList() {
        return mColoredVisualResourceItemList;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return mSourceDrawableResourceId;
    }
}