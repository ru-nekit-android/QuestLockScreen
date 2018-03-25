package ru.nekit.android.qls.domain.model.resources

import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceWithGroupHolder

enum class CoinVisualResourceCollection constructor(val nomination: Int, val relativeSizeValue: Int) :
        IVisualResourceWithGroupHolder {

    ONE(1, 205),
    TWO(2, 230),
    FIVE(5, 250),
    TEN(10, 220);

    override val id: Int
        get() = ordinal

    override val groups: List<ResourceGroupCollection>
        get() = listOf(ResourceGroupCollection.COINS)

    //override val collection: List<CoinVisualResourceCollection> = CoinVisualResourceCollection.values().toList()

    companion object {

        fun getById(id: Int): CoinVisualResourceCollection {
            return values()[id]
        }

        val maxRelativeSizeValue: Int
            get() = values()
                    .asSequence()
                    .map { it.relativeSizeValue }
                    .max()
                    ?: 0

    }

}