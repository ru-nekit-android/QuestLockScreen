package ru.nekit.android.qls.quest.resources.struct;

import android.support.annotation.NonNull;
import android.util.Pair;

import ru.nekit.android.qls.quest.model.ColorModel;

public class PairColorStruct {

    private Pair<ColorModel, ColorModel> mData;

    public PairColorStruct(@NonNull ColorModel primaryColor,
                           @NonNull ColorModel secondaryColor) {
        mData = new Pair<>(primaryColor, secondaryColor);
    }

    public PairColorStruct(int primaryId,
                           int secondaryId) {
        mData = new Pair<>(ColorModel.getById(primaryId), ColorModel.getById(secondaryId));
    }

    @NonNull
    public ColorModel getPrimaryColorModel() {
        return mData.first;
    }

    @NonNull
    public ColorModel getSecondaryColorModel() {
        return mData.second;
    }
}