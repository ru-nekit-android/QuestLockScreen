package ru.nekit.android.qls.quest.resources.representation.common

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import ru.nekit.android.qls.data.providers.*
import ru.nekit.android.qls.domain.model.resources.*
import ru.nekit.android.qls.domain.model.resources.common.IRepresentation
import ru.nekit.android.qls.domain.model.resources.common.IResourceGroupHolder
import ru.nekit.android.qls.domain.utils.Declension
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualResourceProvider
import ru.nekit.android.qls.quest.resources.common.ILocalizedAdjectiveStringResourceProvider
import ru.nekit.android.qls.quest.resources.common.ILocalizedNounStringResourceHolder
import ru.nekit.android.qls.quest.resources.representation.getRepresentation
import ru.nekit.android.qls.quest.resources.struct.ColorfullVisualResourceStruct

open class ResourceRepresentationProvider<in Key : Enum<*>, Value> {

    private val linkMap: MutableMap<Key, Value> = HashMap()

    protected fun createRepresentation(key: Key, value: Value) =
            linkMap.put(key, value)

    fun getRepresentation(item: Key): Value {
        var result: Value? = null
        for ((key, value) in linkMap) {
            if (key.ordinal == item.ordinal) {
                result = value
                break
            }
        }
        return result!!
    }

}

open class StringRepresentationProvider<in Key : Enum<*>> : ResourceRepresentationProvider<Key,
        StringRepresentation>() {

    private fun createRepresentation(key: Key, string: String) {
        createRepresentation(key, StringRepresentation(string))
    }

}

open class StringListRepresentationProvider<in Key : Enum<*>> : ResourceRepresentationProvider<Key,
        StringListRepresentation>() {

    private fun createRepresentation(key: Key, strings: List<String>) {
        createRepresentation(key, StringListRepresentation(strings))
    }

    private fun createRepresentation(key: Key, string: String) {
        createRepresentation(key, StringListRepresentation(listOf(string)))
    }
}

open class StringIdRepresentationProvider<in Key : Enum<*>> : ResourceRepresentationProvider<Key,
        StringIdRepresentation>() {


    private fun createRepresentation(key: Key, stringResourceId: Int) {
        createRepresentation(key, stringResourceId)
    }

}

open class StringListIdRepresentationProvider<in Key : Enum<*>> : ResourceRepresentationProvider<Key,
        StringListIdRepresentation>() {

    private fun createRepresentation(key: Key, stringListResourceId: List<Int>) {
        createRepresentation(key, stringListResourceId)
    }

}

open class StringIdRepresentation
internal constructor(
        @param:StringRes
        @field:StringRes
        override val stringResourceId: Int
) : IStringResourceProvider, IRepresentation

open class StringRepresentation
internal constructor(
        override val string: String
) : IStringHolder, IRepresentation

open class StringListRepresentation
internal constructor(
        override val strings: List<String>
) : IStringListHolder, IRepresentation

open class StringListIdRepresentation
internal constructor(
        @param:StringRes
        @field:StringRes
        override val stringListResourceId: List<Int>
) : IStringListResourceProvider, IRepresentation

open class LocalizedNounStringResourceRepresentation internal constructor(
        stringResourceId: Int,
        val gender: Declension.Gender,
        val isPlural: Boolean,
        val ownRule: List<String>
) : StringIdRepresentation(stringResourceId) {

    fun hasOwnRule(): Boolean = ownRule.isNotEmpty()

}

class ColorResourceRepresentation
internal constructor(
        @param:ColorRes
        @field:ColorRes
        @get:ColorRes
        override val colorResourceId: Int,
        private val adjectiveStringResourceCollection: LocalizedAdjectiveStringResourceCollection) :
        ILocalizedAdjectiveStringResourceProvider,
        IColorResourceProvider,
        IRepresentation {

    override val stringResourceId: Int
        get() = adjectiveStringResourceCollection.getRepresentation().stringResourceId

    override val localizedStringResource
        get() = adjectiveStringResourceCollection

}

class ChildrenToysRepresentation
internal constructor(
        override val coloredVisualResourceList: List<ColorfullVisualResourceStruct>,
        override val drawableResourceId: Int,
        override val localizedStringResource: LocalizedNounStringResourceCollection
) : IDrawableResourceProvider,
        ILocalizedNounStringResourceHolder,
        IColorfullVisualResourceProvider,
        IRepresentation

class CoinVisualResourceRepresentation
internal constructor(
        val item: CoinVisualResourceCollection,
        @param:DrawableRes
        @field:DrawableRes
        @get:DrawableRes
        override val drawableResourceId: Int,
        override val stringResourceId: Int
) : IDrawableResourceProvider, IStringResourceProvider,
        IRepresentation

class SimpleVisualResourceRepresentation
internal constructor(
        val item: SimpleVisualResourceCollection,
        @param:DrawableRes
        @field:DrawableRes
        @get:DrawableRes
        override val drawableResourceId: Int,
        override val stringResourceId: Int
) : IDrawableResourceProvider,
        IResourceGroupHolder,
        IStringResourceProvider,
        ILocalizedNounStringResourceHolder,
        IRepresentation {

    private var internalLocalizedStringResource: LocalizedNounStringResourceCollection? = null

    constructor(item: SimpleVisualResourceCollection,
                drawableResourceId: Int,
                localizedStringResource: LocalizedNounStringResourceCollection) :
            this(item, drawableResourceId, -1) {
        internalLocalizedStringResource = localizedStringResource
    }

    override val localizedStringResource: LocalizedNounStringResourceCollection?
        get() = internalLocalizedStringResource

    override val groups: List<ResourceGroupCollection>
        get() = item.groups

}