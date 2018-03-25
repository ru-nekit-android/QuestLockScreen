package ru.nekit.android.qls.quest.resources.struct

import android.util.Pair

import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection

class PairColorStruct {

    private var data: Pair<ColorResourceCollection, ColorResourceCollection>? = null

    val primaryColorModel: ColorResourceCollection
        get() = data!!.first

    val secondaryColorModel: ColorResourceCollection
        get() = data!!.second

    constructor(primaryColor: ColorResourceCollection,
                secondaryColor: ColorResourceCollection) {
        data = Pair(primaryColor, secondaryColor)
    }

    constructor(primaryId: Int,
                secondaryId: Int) {
        data = Pair(ColorResourceCollection.getById(primaryId), ColorResourceCollection.getById(secondaryId))
    }
}

