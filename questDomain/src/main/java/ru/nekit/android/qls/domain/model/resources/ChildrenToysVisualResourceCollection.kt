package ru.nekit.android.qls.domain.model.resources

import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.*
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceWithGroupHolder

enum class ChildrenToysVisualResourceCollection(vararg groups: ResourceGroupCollection) :
        IVisualResourceWithGroupHolder {

    CAR(BOY, CHILDREN_TOY),
    DOLL_BOOTS(GIRL, CHILDREN_TOY),
    DOLL_SKIRT(GIRL, CHILDREN_TOY),
    DOLL_BLOUSE(GIRL, CHILDREN_TOY);

    override val groups: List<ResourceGroupCollection> = groups.toList()

    override val id: Int
        get() = ordinal

    //override val collection: List<ChildrenToysVisualResourceCollection> = values().toList()

    companion object {

        fun getById(id: Int): SimpleVisualResourceCollection {
            return SimpleVisualResourceCollection.values()[id]
        }

    }
}

