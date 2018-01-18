package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualQuestResourceHolder;
import ru.nekit.android.qls.quest.resources.common.ILocalizedStringQuestResourceHolder;
import ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct;

import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.NONE;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.PRIMARY;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.PRIMARY_INVERSE;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.SECONDARY;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.SECONDARY_INVERSE;

public enum ChildrenToysVisualQuestResourceCollection implements
        IColorfullVisualQuestResourceHolder, ILocalizedStringQuestResourceHolder {

    CAR(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_car_background, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_car_content, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_car_foreground, SECONDARY)),
            R.drawable.qvri_car,
            LocalizedStringResourceCollection.CAR,
            VisualQuestResourceGroupCollection.BOY,
            VisualQuestResourceGroupCollection.CHILDREN_TOY),

    DOLL_BOOTS(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_skirt, PRIMARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)),
            R.drawable.qvri_girl,
            LocalizedStringResourceCollection.BOOTS,
            VisualQuestResourceGroupCollection.GIRL,
            VisualQuestResourceGroupCollection.CHILDREN_TOY),

    DOLL_SKIRT(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_skirt, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)),
            R.drawable.qvri_girl,
            LocalizedStringResourceCollection.DOLL_SKIRT,
            VisualQuestResourceGroupCollection.GIRL,
            VisualQuestResourceGroupCollection.CHILDREN_TOY),

    DOLL_BLOUSE(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_skirt, SECONDARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_blouse, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)),
            R.drawable.qvri_girl,
            LocalizedStringResourceCollection.DOLL_BLOUSE,
            VisualQuestResourceGroupCollection.GIRL,
            VisualQuestResourceGroupCollection.CHILDREN_TOY);

    private final List<ColorfullQuestVisualResourceStruct> mColorfullQuestVisualResourceStructList;
    @DrawableRes
    private int mSourceDrawableResourceId;
    @Nullable
    private VisualQuestResourceGroupCollection[] mGroups;
    @NonNull
    private LocalizedStringResourceCollection mLocalizedStringResourceCollection;

    ChildrenToysVisualQuestResourceCollection(@NonNull List<ColorfullQuestVisualResourceStruct> colorfullQuestVisualResourceStructList,
                                              @DrawableRes int sourceDrawableResourceId,
                                              @NonNull LocalizedStringResourceCollection localizedStringResourceCollection,
                                              @Nullable VisualQuestResourceGroupCollection... groups) {
        mColorfullQuestVisualResourceStructList = colorfullQuestVisualResourceStructList;
        mSourceDrawableResourceId = sourceDrawableResourceId;
        mLocalizedStringResourceCollection = localizedStringResourceCollection;
        mGroups = groups;
    }

    int getId() {
        return ordinal();
    }

    @NonNull
    public LocalizedStringResourceCollection getLocalStringResource() {
        return mLocalizedStringResourceCollection;
    }

    @NonNull
    public String getName(@NonNull Context context) {
        return mLocalizedStringResourceCollection.getName(context);
    }

    @Nullable
    public VisualQuestResourceGroupCollection[] getGroups() {
        return mGroups;
    }

    @NonNull
    @Override
    public List<ColorfullQuestVisualResourceStruct> getColoredVisualResourceList() {
        return mColorfullQuestVisualResourceStructList;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return mSourceDrawableResourceId;
    }
}