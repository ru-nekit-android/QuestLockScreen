package ru.nekit.android.qls.domain.model.resources

import java.util.*

enum class ResourceGroupCollection constructor(vararg parents: ResourceGroupCollection) {

    CHOICE,
    FRUIT,
    MATH_OPERATOR,
    BERRY(FRUIT, CHOICE),
    POMUM(FRUIT, CHOICE),
    SEASONS(CHOICE),
    COINS(CHOICE),
    SEX,
    GIRL(SEX),
    BOY(SEX),
    CHILDREN_TOY;

    private val parents: List<ResourceGroupCollection> = parents.toList()

    val id: Int
        get() = ordinal

    val children: List<ResourceGroupCollection>
        get() {
            val children = ArrayList<ResourceGroupCollection>()
            for (group in values()) {
                group.parents.asSequence()
                        .filter { it == this }
                        .forEach { children.add(group) }
            }
            return children
        }

    fun hasParent(group: ResourceGroupCollection): Boolean {
        var result = this == group
        if (!result) {
            val parents = this.parents
            for (parentItem in parents) {
                result = parentItem.hasParent(group)
                if (result) {
                    break
                }
            }
        }
        return result
    }

    companion object {

        fun getGroup(id: Int) = getById(id)

        fun getById(id: Int): ResourceGroupCollection {
            return values()[id]
        }

    }
}