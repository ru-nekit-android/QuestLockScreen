package ru.nekit.android.qls.window.common

import android.support.annotation.CallSuper
import android.support.annotation.StyleRes
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.providers.IQuestContextSupport
import ru.nekit.android.window.Window
import ru.nekit.android.window.WindowContentViewHolder

abstract class QuestWindowMediator(override var questContext: QuestContext) : IQuestContextSupport {

    private lateinit var window: Window
    private lateinit var windowContentViewHolder: WindowContentViewHolder

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    @get:StyleRes
    protected abstract val windowStyleId: Int

    protected abstract fun createWindowContent(): Single<WindowContentViewHolder>

    fun openWindow(body: () -> Unit) {
        autoDispose {
            createWindowContent().subscribe { it ->
                windowContentViewHolder = it
                QuestWindow(questContext,
                        getName(),
                        windowContentViewHolder,
                        windowStyleId).also {
                    window = it
                    listenForWindowEvent {
                        if (it == QuestWindowEvent.CLOSED)
                            destroy()
                    }
                }.open()
                body()
            }
        }
    }

    abstract fun getName(): String

    @CallSuper
    protected open fun destroy() {
        dispose()
    }

    protected fun closeWindow(position: String) {
        window.close(position)
    }

}