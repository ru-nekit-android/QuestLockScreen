package ru.nekit.android.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface IAutoDispose {

    companion object {
        internal const val DEFAULT_NAME = "default"
    }

    var disposableMap: MutableMap<String, CompositeDisposable>

    fun disposable(name: String?): CompositeDisposable {
        var disposable = disposableMap[getName(name)]
        if (disposable == null) {
            disposable = CompositeDisposable()
            disposableMap[getName(name)] = disposable
        }
        return disposable
    }

    fun autoDispose(name: String? = null, body: () -> Disposable) {
        disposable(name).apply {
            this.add(body())
        }
    }

    private fun init(name: String? = null) {
        val localName = getName(name)
        if (!disposableMap.containsKey(localName))
            disposableMap[localName] = CompositeDisposable()
    }

    private fun getName(name: String? = null) = name ?: DEFAULT_NAME

    fun autoDisposeList(name: String? = null, vararg values: Disposable) {
        values.forEach {
            disposable(name).apply {
                add(it)
            }
        }
    }

    fun autoDisposeList(vararg values: Disposable) = autoDisposeList(null, *values)

    fun dispose() {
        dispose(null)
    }

    fun dispose(name: String? = null) {
        if (name == null) {
            disposableMap.forEach {
                dispose(it.key)
            }
            disposableMap.clear()
        } else {
            disposableMap[getName(name)]?.apply {
                clear()
                disposableMap.remove(getName(name))
            }
        }
    }
}