package ru.nekit.android.qls.quest.resources.struct;

import android.support.annotation.NonNull;
import android.util.Pair;

import ru.nekit.android.qls.quest.resources.collections.ColorResourceCollection;

public class PairColorStruct {

    private Pair<ColorResourceCollection, ColorResourceCollection> mData;

    public PairColorStruct(@NonNull ColorResourceCollection primaryColor,
                           @NonNull ColorResourceCollection secondaryColor) {
        mData = new Pair<>(primaryColor, secondaryColor);
    }

    public PairColorStruct(int primaryId,
                           int secondaryId) {
        mData = new Pair<>(ColorResourceCollection.getById(primaryId), ColorResourceCollection.getById(secondaryId));
    }

    @NonNull
    public ColorResourceCollection getPrimaryColorModel() {
        return mData.first;
    }

    @NonNull
    public ColorResourceCollection getSecondaryColorModel() {
        return mData.second;
    }
}