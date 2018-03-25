package ru.nekit.android.qls.domain.model.resources

import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.*
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceWithGroupHolder

enum class SimpleVisualResourceCollection constructor(vararg groups: ResourceGroupCollection) :
        IVisualResourceWithGroupHolder {

    MINUS(MATH_OPERATOR),
    PLUS(MATH_OPERATOR),
    EQUAL(MATH_OPERATOR),
    ORANGE(POMUM),
    STRAWBERRY(BERRY),
    APPLE(POMUM),
    PEAR(POMUM),
    CHERRY(BERRY),
    RASPBERRY(BERRY),
    PINEAPPLE(POMUM),
    BLACKBERRY(BERRY),
    WINTER(SEASONS),
    SPRING(SEASONS),
    SUMMER(SEASONS),
    FALL(SEASONS);

    override val groups: List<ResourceGroupCollection> = groups.toList()

    override val id: Int
        get() = ordinal

    companion object {

        fun getById(id: Int): SimpleVisualResourceCollection {
            return values()[id]
        }

    }

}