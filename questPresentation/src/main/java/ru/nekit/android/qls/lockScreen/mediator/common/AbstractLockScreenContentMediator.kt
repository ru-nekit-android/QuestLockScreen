package ru.nekit.android.qls.lockScreen.mediator.common

import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.qls.quest.providers.IQuestContextProvider

abstract class AbstractLockScreenContentMediator : IQuestContextProvider {

    override var disposable: CompositeDisposable = CompositeDisposable()

    abstract val viewHolder: ILockScreenContentViewHolder

    abstract fun deactivate()

    abstract fun detachView()

    abstract fun attachView()

}