package ru.nekit.android.qls.quest.view.mediator.answer

import android.support.annotation.CallSuper
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.ReplaySubject
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.answer.IAnswerVariantAdapter

//ver 1.0
open class ButtonListAnswerMediator(private val buttonListAdapter: IAnswerVariantAdapter<*>? = null) :
        AnswerMediator(), IButtonListAnswerMediator {

    override val answerButtonPublisher: ReplaySubject<Optional<View>> = ReplaySubject.create<Optional<View>>()

    @CallSuper
    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
    }

    private fun createButtonInternal(label: String, tag: Any, isFirst: Boolean, isLast: Boolean): View {
        val params = LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        return createButton(label, tag, params, isFirst, isLast).apply {
            layoutParams = params
            if (this is TextView) {
                text = label
            }
            this.tag = tag
            autoDispose {
                clicks().subscribe({
                    answerPublisher.onNext(tag)
                })
            }
        }
    }

    override fun deactivate() {
        answerButtonPublisher.onNext(Optional(null))
        super.deactivate()
    }

    protected open fun createButton(label: String,
                                    tag: Any,
                                    layoutParams: LinearLayout.LayoutParams,
                                    isFirst: Boolean,
                                    isLast: Boolean): View {
        return AppCompatButton(questContext)
    }

    protected fun refillButtonListWithAvailableVariants(shuffle: Boolean) {
        answerButtonPublisher.onNext(Optional(null))
        fillButtonListWithAvailableVariants(shuffle)
    }

    protected fun fillButtonListWithAvailableVariants() {
        fillButtonListWithAvailableVariants(false)
    }

    protected fun fillButtonListWithAvailableVariants(shuffle: Boolean) {
        var availableVariants = quest.availableAnswerVariants
        if (shuffle) {
            availableVariants = availableVariants!!.shuffled()
        }
        if (availableVariants != null) {
            val length = availableVariants.size
            for (i in 0 until length) {
                val variant = availableVariants[i]
                val label = if (buttonListAdapter == null)
                    variant.toString()
                else
                    @Suppress("UNCHECKED_CAST")
                    (buttonListAdapter as IAnswerVariantAdapter<Any>).adapt(questContext, variant)
                if (label != null) {
                    answerButtonPublisher.onNext(Optional(createButtonInternal(label, variant,
                            i == 0,
                            i == length - 1)))
                }
            }
        }
    }
}