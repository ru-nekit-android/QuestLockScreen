package ru.nekit.android.qls.window.common

import android.support.annotation.CallSuper
import android.support.annotation.StyleRes
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.providers.IEventListenerProvider
import ru.nekit.android.window.Window
import ru.nekit.android.window.WindowContentViewHolder

abstract class QuestWindowMediator(protected val questContext: QuestContext) : IEventListenerProvider {

    private lateinit var window: Window
    private lateinit var windowContentViewHolder: WindowContentViewHolder

    override lateinit var disposable: CompositeDisposable
    override val eventListener: IEventListener = questContext.eventListener

    @get:StyleRes
    protected abstract val windowStyleId: Int

    protected abstract fun createWindowContent(): Single<WindowContentViewHolder>

    fun openWindow(body: () -> Unit) {
        disposable = CompositeDisposable()
        autoDispose {
            createWindowContent().subscribe { it ->
                windowContentViewHolder = it
                QuestWindow(questContext,
                        "QuestWindowMediator",
                        windowContentViewHolder,
                        windowStyleId).also {
                    window = it
                    eventListener.listen(this, QuestWindowEvent::class.java) {
                        if (it == QuestWindowEvent.CLOSED)
                            destroy()
                    }
                }.open()
                body()
            }
        }
    }

    @CallSuper
    protected open fun destroy() {
        dispose()
    }

    protected fun closeWindow(position: String) {
        window.close(position)
    }

}