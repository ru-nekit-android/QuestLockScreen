package ru.nekit.android.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface IAutoDispose {

    var disposable: CompositeDisposable

    fun autoDispose(body: () -> Disposable) {
        disposable.add(body())
    }

    fun autoDisposeList(body: () -> List<Disposable>) {
        body().forEach {
            disposable.add(it)
        }
    }

    fun dispose() {
        disposable.clear()
    }
}