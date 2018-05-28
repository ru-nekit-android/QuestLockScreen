package ru.nekit.android.qls.lockScreen.mediator

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.Completable
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.useCases.TransitionChoreographUseCases
import ru.nekit.android.qls.lockScreen.mediator.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.lockScreen.mediator.common.ILockScreenContentViewHolder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.utils.ViewHolder
import ru.nekit.android.utils.responsiveClicks

class LevelUpContentMediator(override var questContext: QuestContext) :
        AbstractLockScreenContentMediator() {

    override var viewHolder: LockScreenLevelUpViewContentHolder =
            LockScreenLevelUpViewContentHolder(questContext)

    init {
        autoDispose {
            viewHolder.okButton.responsiveClicks {
                TransitionChoreographUseCases.doNextTransition()
            }
        }
    }

    override fun deactivate() {
        dispose()
    }

    override fun detachView() {
        viewHolder.content.removeAllViews()
    }

    override fun attachView(callback: () -> Unit) = autoDispose {
        Completable.fromAction {
            setDefaultSettingsForTools()
            viewHolder.titleView.text = questContext.getString(R.string.title_new_level)
        }.subscribe(callback)
    }

}

private fun setDefaultSettingsForTools() {}

class LockScreenLevelUpViewContentHolder internal constructor(context: Context) :
        ViewHolder(context, R.layout.layout_lock_screen_level_up_view_container), ILockScreenContentViewHolder {

    internal var okButton: Button = view.findViewById<View>(R.id.btn_ok) as Button
    override val contentContainer: View = view.findViewById(R.id.container_content)
    override val titleContentContainer: View = view.findViewById(R.id.container_title)
    internal val content: ViewGroup = view.findViewById(R.id.content) as ViewGroup
    internal val titleView: TextView = view.findViewById(R.id.tv_title) as TextView

    init {
        (view as ViewGroup).removeAllViews()
    }
}
