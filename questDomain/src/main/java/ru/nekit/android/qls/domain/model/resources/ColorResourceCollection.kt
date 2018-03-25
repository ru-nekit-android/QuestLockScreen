package ru.nekit.android.qls.domain.model.resources

import ru.nekit.android.qls.domain.model.resources.common.IResourceHolder

enum class ColorResourceCollection : IResourceHolder {

    WHITE,
    BLACK,
    RED,
    GREEN;

    override val id: Int
        get() = ordinal

    companion object {

        fun getById(id: Int): ColorResourceCollection {
            return values()[id]
        }
    }
}