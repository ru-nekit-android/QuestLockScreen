package ru.nekit.android.data

import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.domain.repository.IReactiveStringKeyValueStore
import ru.nekit.android.domain.repository.ISingleKeyValueStore
import ru.nekit.android.domain.repository.IStringKeyValueStore

abstract class KeyValueStore<Type>(private val sharedPreferences: SharedPreferences) : IStringKeyValueStore<Type> {
    override fun contains(key: String): Boolean = sharedPreferences.contains(key)
    override fun remove(key: String) = sharedPreferences.edit().remove(key).apply()
    fun clear() = sharedPreferences.edit().clear().apply()
}

abstract class SingleKeyValueStore<Type>(private val key: String, private val sharedPreferences: SharedPreferences) : ISingleKeyValueStore<Type> {
    override fun contains(): Boolean = sharedPreferences.contains(key)
    override fun remove() = sharedPreferences.edit().remove(key).apply()
}

open class StringKeyStringValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<String?>(sharedPreferences) {

    override fun put(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun get(key: String): String = sharedPreferences.getString(key, "")

    override fun get(key: String, default: String?): String? = sharedPreferences.getString(key, default)

}

open class ReactiveStringKeyStringValueStore(sharedPreferences: SharedPreferences) : IReactiveStringKeyValueStore<String> {

    private val store: StringKeyStringValueStore = StringKeyStringValueStore(sharedPreferences)

    override fun put(key: String, value: String): Completable = Completable.fromRunnable {
        store.put(key, value)
    }

    override fun get(key: String): Single<Optional<String>> = Single.fromCallable {
        Optional(store.get(key))
    }

    override fun contains(key: String): Single<Boolean> = Single.fromCallable {
        store.contains(key)
    }

    override fun remove(key: String): Completable = Completable.fromRunnable {
        store.remove(key)
    }
}

class ReactiveStringKeyBooleanValueStore(sharedPreferences: SharedPreferences) : IReactiveStringKeyValueStore<Boolean> {

    private val store: BooleanKeyValueStore = BooleanKeyValueStore(sharedPreferences)

    override fun put(key: String, value: Boolean): Completable = Completable.fromRunnable {
        store.put(key, value)
    }

    override fun get(key: String): Single<Optional<Boolean>> = Single.fromCallable {
        Optional(store.get(key))
    }

    override fun contains(key: String): Single<Boolean> = Single.fromCallable {
        store.contains(key)
    }

    override fun remove(key: String): Completable = Completable.fromRunnable {
        store.remove(key)
    }
}

class StringKeyLongValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<Long>(sharedPreferences) {
    override fun put(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun get(key: String): Long = sharedPreferences.getLong(key, 0)

    override fun get(key: String, default: Long): Long = sharedPreferences.getLong(key, default)
}

open class StringKeyIntValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<Int>(sharedPreferences) {

    override fun put(key: String, value: Int) =
            sharedPreferences.edit().putInt(key, value).apply()

    override fun get(key: String): Int = sharedPreferences.getInt(key, 0)

    override fun get(key: String, default: Int): Int = sharedPreferences.getInt(key, default)

}

class IntSingleKeyValueStore(private val key: String, private val sharedPreferences: SharedPreferences) : SingleKeyValueStore<Int>(key, sharedPreferences) {

    override fun get(default: Int): Int? = sharedPreferences.getInt(key, default)

    override fun put(value: Int) =
            sharedPreferences.edit().putInt(key, value).apply()

    override fun get(): Int = sharedPreferences.getInt(key, 0)

}

class BooleanKeyValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<Boolean>(sharedPreferences) {

    override fun put(key: String, value: Boolean) =
            sharedPreferences.edit().putBoolean(key, value).apply()

    override fun get(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    override fun get(key: String, default: Boolean): Boolean = sharedPreferences.getBoolean(key, default)
}

class FloatKeyValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<Float>(sharedPreferences) {

    override fun put(key: String, value: Float) =
            sharedPreferences.edit().putFloat(key, value).apply()

    override fun get(key: String): Float = sharedPreferences.getFloat(key, 0F)


    override fun get(key: String, default: Float): Float = sharedPreferences.getFloat(key, default)
}