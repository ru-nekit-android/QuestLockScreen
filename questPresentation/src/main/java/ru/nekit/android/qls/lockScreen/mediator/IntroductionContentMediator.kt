package ru.nekit.android.qls.lockScreen.mediator

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.useCases.CommitNextTransitionUseCase
import ru.nekit.android.qls.lockScreen.mediator.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.lockScreen.mediator.common.ILockScreenContentViewHolder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.utils.ViewHolder

//ver 1.0
class IntroductionContentMediator(override var questContext: QuestContext) :
        AbstractLockScreenContentMediator() {

    override var disposable: CompositeDisposable = CompositeDisposable()

    override var viewHolder: LockScreenIntroductionViewContentHolder = LockScreenIntroductionViewContentHolder(questContext)

    init {
        autoDisposeList {
            listOf(
                    viewHolder.startButton.clicks().subscribe {
                        CommitNextTransitionUseCase(questContext.repository,
                                eventSender,
                                questContext.schedulerProvider).use()
                    },
                    viewHolder.menuButton.clicks().subscribe {
                        //MenuWindowMediator.openWindow(questContext)
                    }
            )
        }
    }

    override fun deactivate() {
        dispose()
    }

    override fun detachView() {
        viewHolder.content.removeAllViews()
    }

    override fun attachView() {
        viewHolder.titleView.text = questContext.getString(R.string.title_introduction)

        questContext.getRemainingAmountForReaching { it ->
            System.out.print(it)
        }
    }

    class LockScreenIntroductionViewContentHolder internal constructor(context: Context) :
            ViewHolder(context, R.layout.layout_lock_screen_intoduction_content), ILockScreenContentViewHolder {

        internal val startButton: View = view.findViewById(R.id.btn_start)
        internal val menuButton: View = view.findViewById(R.id.btn_menu)
        override val contentContainer: View = view.findViewById(R.id.container_content)
        override val titleContentContainer: View = view.findViewById(R.id.container_title)
        internal val content: ViewGroup = view.findViewById(R.id.content) as ViewGroup
        internal val titleView: TextView = view.findViewById(R.id.tv_title) as TextView

        init {
            (view as ViewGroup).removeAllViews()
        }
    }
}