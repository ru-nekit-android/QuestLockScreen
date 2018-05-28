package ru.nekit.android.utils

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit.MILLISECONDS

fun View.throttleClicks(delay: Long): Observable<Unit> = clicks().throttleFirst(delay,
        MILLISECONDS)

fun View.throttleClicks(): Observable<Unit> = throttleClicks(Delay.THROTTLE.get(context))

fun View.throttleClicks(delay: Long, body: (Unit) -> Unit): Disposable =
        throttleClicks(delay).subscribe(body)

fun View.throttleClicks(body: (Unit) -> Unit): Disposable =
        throttleClicks(Delay.THROTTLE.get(context)).subscribe(body)

fun View.responsiveClicks(delay: Long, body: () -> Unit): Disposable =
        responsiveClicks(delay).subscribe {
            body()
        }

fun View.responsiveClicks(delay: Long): Observable<Unit> = clicks().switchMap { Observable.fromCallable { Vibrate.make(context, delay) } }

fun View.responsiveClicks(): Observable<Unit> = clicks().switchMap { Observable.fromCallable { Vibrate.make(context, Delay.CLICK.get(context)) } }

fun View.responsiveClicks(body: () -> Unit) =
        responsiveClicks(Delay.CLICK.get(context), body)
