package ru.nekit.android.data.support

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.Property
import io.objectbox.converter.PropertyConverter
import io.objectbox.query.Query
import io.reactivex.*

abstract class ObjectBoxSupport<Entity>(val boxStore: BoxStore) {

    protected abstract fun createBox(): Box<Entity>

    inline fun <reified Entity> initBox(): Box<Entity> = boxStore.boxFor(Entity::class.java)

    protected fun boxCompletableUsing(body: (Box<Entity>) -> CompletableSource) =
            boxCompletableUsing(createBox(), body)

    protected fun boxCompletableUsingFromRunnable(body: (Box<Entity>) -> Unit) =
            boxCompletableUsingFromRunnable(createBox(), body)

    protected fun <T> boxSingleUsing(body: (Box<Entity>) -> Single<T>) =
            boxSingleUsing(createBox(), body)

    protected fun <T> boxSingleUsingWithCallable(body: (Box<Entity>) -> T) =
            boxSingleUsingWithCallable(createBox(), body)

    protected fun <T> boxFlowableUsingWithCallable(body: (Box<Entity>) -> T) =
            boxFlowableUsingWithCallable(createBox(), body)

    protected fun Box<Entity>.queryBy(property: Property, value: String): Query<Entity> =
            query().equal(property, value).build()

    protected fun Box<Entity>.queryBy(property: Property, value: Boolean): Query<Entity> =
            query().equal(property, value).build()

    protected fun Box<Entity>.queryBy(property: Property, value: Long): Query<Entity> =
            query().equal(property, value).build()

    private fun <T> boxCompletableUsing(box: Box<T>, body: (Box<T>) -> CompletableSource): Completable =
            Completable.using({ box },
                    { body(it) },
                    { it.closeThreadResources() })

    private fun <T> boxCompletableUsingFromRunnable(box: Box<T>, body: (Box<T>) -> Unit): Completable =
            boxCompletableUsing(box, { Completable.fromRunnable { body(box) } })

    private fun <T, R> boxSingleUsing(box: Box<T>, body: (Box<T>) -> SingleSource<R>): Single<R> =
            Single.using({ box },
                    { body(it) },
                    { it.closeThreadResources() })

    private fun <T, R> boxFlowableUsing(box: Box<T>, body: (Box<T>) -> Flowable<R>): Flowable<R> =
            Flowable.using({ box },
                    { body(it) },
                    { it.closeThreadResources() })

    protected fun <R> boxUsing(body: (Box<Entity>) -> R): R {
        val box = createBox()
        val result: R = body(box)
        box.closeThreadResources()
        return result
    }

    private fun <T, R> boxSingleUsingWithCallable(box: Box<T>, body: (Box<T>) -> R): Single<R> =
            boxSingleUsing(box, { Single.fromCallable { body(box) } })

    private fun <T, R> boxFlowableUsingWithCallable(box: Box<T>, body: (Box<T>) -> R): Flowable<R> =
            boxFlowableUsing(box, { Flowable.fromCallable { body(box) } })

    open fun removeAll() {
        createBox().removeAll()
    }
}

abstract class PropertyListConverter<T> : PropertyConverter<List<T>, String> {

    private val converter: PropertyConverter<T, String> = this.createPropertyConverter()

    abstract fun createPropertyConverter(): PropertyConverter<T, String>

    override fun convertToEntityProperty(databaseValue: String): List<T> =
            if (databaseValue.isNotEmpty()) databaseValue.split(",").map { converter.convertToEntityProperty(it) } else
                ArrayList()

    override fun convertToDatabaseValue(entityProperty: List<T>): String =
            if (entityProperty.isNotEmpty())
                entityProperty.joinToString(",", transform = {
                    converter.convertToDatabaseValue(it)
                })
            else ""
}

abstract class PropertyListListConverter<T> : PropertyConverter<List<List<T>>, String> {

    private val converter: PropertyConverter<T, String> = this.createPropertyConverter()

    abstract fun createPropertyConverter(): PropertyConverter<T, String>

    override fun convertToEntityProperty(databaseValue: String): List<List<T>> {
        val result = ArrayList<ArrayList<T>>()
        if (databaseValue == "") return result
        val splitList = databaseValue.split(";")
        splitList.forEach {
            val subList = ArrayList<T>()
            it.split(",").map { subList.add(converter.convertToEntityProperty(it)) }
            result.add(subList)
        }
        return result
    }

    override fun convertToDatabaseValue(entityProperty: List<List<T>>): String {
        val stringBufferList = ArrayList<String>()
        entityProperty.forEach {
            stringBufferList += it.joinToString(",", transform = {
                converter.convertToDatabaseValue(it)
            })
        }
        return stringBufferList.joinToString(";")

    }

}