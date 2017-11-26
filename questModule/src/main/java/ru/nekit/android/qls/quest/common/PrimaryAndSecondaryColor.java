package ru.nekit.android.qls.quest.common;

import android.support.annotation.NonNull;
import android.util.Pair;

import ru.nekit.android.qls.quest.model.ColorModel;

public class PrimaryAndSecondaryColor {

    private Pair<ColorModel, ColorModel> mData;

    public PrimaryAndSecondaryColor(@NonNull ColorModel primaryColor,
                                    @NonNull ColorModel secondaryColor) {
        mData = new Pair<>(primaryColor, secondaryColor);
    }

    public PrimaryAndSecondaryColor(int primaryId,
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