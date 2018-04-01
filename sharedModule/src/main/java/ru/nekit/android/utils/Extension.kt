package ru.nekit.android.utils

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun View.throttleClicks(body: () -> Unit): Disposable = clicks().throttleFirst(1000,
        TimeUnit.MILLISECONDS).subscribe { body() }

fun View.throttleClicks(): Observable<Unit> = clicks().throttleFirst(1000,
        TimeUnit.MILLISECONDS)