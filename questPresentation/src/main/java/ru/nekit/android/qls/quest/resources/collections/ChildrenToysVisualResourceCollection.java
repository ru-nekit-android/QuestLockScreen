package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualResourceHolder;
import ru.nekit.android.qls.quest.resources.common.ILocalizedNounStringResourceHolder;
import ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct;

import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.NONE;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.PRIMARY;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.PRIMARY_INVERSE;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.SECONDARY;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.SECONDARY_INVERSE;

public enum ChildrenToysVisualResourceCollection implements
        IColorfullVisualResourceHolder, ILocalizedNounStringResourceHolder {

    CAR(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_car_background, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_car_content, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_car_foreground, SECONDARY)),
            R.drawable.qvri_car,
            LocalizedNounStringResourceCollection.CAR,
            VisualResourceGroupCollection.BOY,
            VisualResourceGroupCollection.CHILDREN_TOY),

    DOLL_BOOTS(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_skirt, PRIMARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)),
            R.drawable.qvri_girl,
            LocalizedNounStringResourceCollection.BOOTS,
            VisualResourceGroupCollection.GIRL,
            VisualResourceGroupCollection.CHILDREN_TOY),

    DOLL_SKIRT(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_skirt, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)),
            R.drawable.qvri_girl,
            LocalizedNounStringResourceCollection.DOLL_SKIRT,
            VisualResourceGroupCollection.GIRL,
            VisualResourceGroupCollection.CHILDREN_TOY),

    DOLL_BLOUSE(Arrays.asList(
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_skirt, SECONDARY_INVERSE),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_blouse, PRIMARY),
            new ColorfullQuestVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)),
            R.drawable.qvri_girl,
            LocalizedNounStringResourceCollection.DOLL_BLOUSE,
            VisualResourceGroupCollection.GIRL,
            VisualResourceGroupCollection.CHILDREN_TOY);

    private final List<ColorfullQuestVisualResourceStruct> mColorfullQuestVisualResourceStructList;
    @DrawableRes
    private int mSourceDrawableResourceId;
    @Nullable
    private VisualResourceGroupCollection[] mGroups;
    @NonNull
    private LocalizedNounStringResourceCollection mLocalizedNounStringResourceCollection;

    ChildrenToysVisualResourceCollection(@NonNull List<ColorfullQuestVisualResourceStruct> colorfullQuestVisualResourceStructList,
                                         @DrawableRes int sourceDrawableResourceId,
                                         @NonNull LocalizedNounStringResourceCollection localizedNounStringResourceCollection,
                                         @Nullable VisualResourceGroupCollection... groups) {
        mColorfullQuestVisualResourceStructList = colorfullQuestVisualResourceStructList;
        mSourceDrawableResourceId = sourceDrawableResourceId;
        mLocalizedNounStringResourceCollection = localizedNounStringResourceCollection;
        mGroups = groups;
    }

    int getId() {
        return ordinal();
    }

    @Nullable
    public LocalizedNounStringResourceCollection getLocalStringResource() {
        return mLocalizedNounStringResourceCollection;
    }

    @NonNull
    public String getString(@NonNull Context context) {
        return mLocalizedNounStringResourceCollection.getString(context);
    }

    @Nullable
    public VisualResourceGroupCollection[] getGroups() {
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