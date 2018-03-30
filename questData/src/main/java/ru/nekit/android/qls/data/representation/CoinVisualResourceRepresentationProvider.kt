package ru.nekit.android.qls.data.representation

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection.*
import ru.nekit.android.questData.R
import ru.nekit.android.questData.R.drawable.*

object CoinVisualResourceRepresentationProvider :
        ResourceRepresentationProvider<CoinVisualResourceCollection,
                CoinVisualResourceRepresentation>() {

    init {
        createRepresentation(ONE, qvri_coin_one, R.string.qvri_coin_one_title)
        createRepresentation(TWO, qvri_coin_two, R.string.qvri_coin_two_title)
        createRepresentation(FIVE, qvri_coin_five, R.string.qvri_coin_five_title)
        createRepresentation(TEN, qvri_coin_ten, R.string.qvri_coin_ten_title)
    }

    private fun createRepresentation(key: CoinVisualResourceCollection,
                                     @DrawableRes drawableResourceId: Int,
                                     @StringRes stringResourceId: Int) {
        createRepresentation(key, CoinVisualResourceRepresentation(key, drawableResourceId,
                stringResourceId))
    }

}

fun CoinVisualResourceCollection.getRepresentation() = CoinVisualResourceRepresentationProvider.getRepresentation(this)
