package ru.nekit.android.qls.lockScreen.mediator.common

import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.qls.quest.providers.IQuestContextSupport

abstract class AbstractLockScreenContentMediator : IQuestContextSupport {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    abstract val viewHolder: ILockScreenContentViewHolder

    abstract fun deactivate()

    abstract fun detachView()

    abstract fun attachView(callback: () -> Unit)

}