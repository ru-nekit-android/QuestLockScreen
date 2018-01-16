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

import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResource.ColorType.NONE;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResource.ColorType.PRIMARY;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResource.ColorType.PRIMARY_INVERSE;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResource.ColorType.SECONDARY;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResource.ColorType.SECONDARY_INVERSE;
import static ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup.BOY;
import static ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup.CHILDREN_TOY;
import static ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup.GIRL;

public enum ChildrenToysVisualResource
        implements IColoredVisualResource {

    CAR(new ArrayList<>(Arrays.asList(
            new ColoredVisualResource(R.drawable.qvri_car_background, PRIMARY),
            new ColoredVisualResource(R.drawable.qvri_car_content, NONE),
            new ColoredVisualResource(R.drawable.qvri_car_foreground, SECONDARY))),
            R.drawable.qvri_car,
            R.string.qvri_car_title,
            BOY,
            CHILDREN_TOY),

    DOLL_BOOTS(new ArrayList<>(Arrays.asList(
            new ColoredVisualResource(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResource(R.drawable.qvri_doll_boots, PRIMARY),
            new ColoredVisualResource(R.drawable.qvri_doll_skirt, PRIMARY_INVERSE),
            new ColoredVisualResource(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
            new ColoredVisualResource(R.drawable.qvri_doll_foreground, NONE))),
            R.drawable.qvri_girl,
            R.string.qvri_doll_boots_title,
            GIRL,
            CHILDREN_TOY),

    DOLL_SKIRT(new ArrayList<>(Arrays.asList(
            new ColoredVisualResource(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResource(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
            new ColoredVisualResource(R.drawable.qvri_doll_skirt, PRIMARY),
            new ColoredVisualResource(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
            new ColoredVisualResource(R.drawable.qvri_doll_foreground, NONE))),
            R.drawable.qvri_girl,
            R.string.qvri_doll_skirt_title,
            GIRL,
            CHILDREN_TOY),

    DOLL_BLOUSE(new ArrayList<>(Arrays.asList(
            new ColoredVisualResource(R.drawable.qvri_doll_background, NONE),
            new ColoredVisualResource(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
            new ColoredVisualResource(R.drawable.qvri_doll_skirt, SECONDARY_INVERSE),
            new ColoredVisualResource(R.drawable.qvri_doll_blouse, PRIMARY),
            new ColoredVisualResource(R.drawable.qvri_doll_foreground, NONE))),
            R.drawable.qvri_girl,
            R.string.qvri_doll_blouse_title,
            GIRL,
            CHILDREN_TOY);

    private final List<ColoredVisualResource> mColoredVisualResourceList;
    @DrawableRes
    private int mSourceDrawableResourceId;
    @StringRes
    private int mTitleResourceId;
    @Nullable
    private VisualResourceGroup[] mGroups;

    ChildrenToysVisualResource(@NonNull List<ColoredVisualResource> coloredVisualResourceList,
                               @DrawableRes int sourceDrawableResourceId,
                               @StringRes int titleResourceId,
                               @Nullable VisualResourceGroup... groups) {
        mColoredVisualResourceList = coloredVisualResourceList;
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
    public List<ColoredVisualResource> getColoredVisualResourceList() {
        return mColoredVisualResourceList;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return mSourceDrawableResourceId;
    }
}