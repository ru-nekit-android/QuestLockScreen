package ru.nekit.android.data

import android.content.SharedPreferences
import ru.nekit.android.domain.repository.IKeyValueStore

abstract class KeyValueStore<Value>(private val sharedPreferences: SharedPreferences) : IKeyValueStore<String, Value> {
    override fun contains(key: String): Boolean = sharedPreferences.contains(key)
}

open class StringKeyValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<String>(sharedPreferences) {

    override fun put(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun get(key: String): String? = sharedPreferences.getString(key, null)

}

open class IntKeyValueStore(private val sharedPreferences: SharedPreferences) : KeyValueStore<Int>(sharedPreferences) {

    override fun put(key: String, value: Int) =
            sharedPreferences.edit().putInt(key, value).apply()

    override fun get(key: String): Int? = sharedPreferences.getInt(key, 0)

}