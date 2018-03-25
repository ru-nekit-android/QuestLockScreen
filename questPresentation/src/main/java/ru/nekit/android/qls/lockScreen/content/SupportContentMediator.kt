package ru.nekit.android.qls.lockScreen.content

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.Transition
import ru.nekit.android.qls.domain.useCases.CommitNextTransitionUseCase
import ru.nekit.android.qls.domain.useCases.GetCurrentTransitionUseCase
import ru.nekit.android.qls.lockScreen.content.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.lockScreen.content.common.ILockScreenContentViewHolder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.utils.ViewHolder

class SupportContentMediator(override var questContext: QuestContext) :
        AbstractLockScreenContentMediator(), View.OnClickListener {

    override lateinit var viewHolder: LockScreenSupportViewContentHolder

    private lateinit var adView: AdView

    init {
        viewHolder = LockScreenSupportViewContentHolder(questContext)
        viewHolder.okButton.setOnClickListener(this)
    }

    override fun deactivate() {
        viewHolder.okButton.setOnClickListener(null)
        dispose()
    }

    override fun detachView() {
        viewHolder.content.removeAllViews()
    }

    override fun attachView() {
        autoDispose {
            GetCurrentTransitionUseCase(questContext.repository,
                    questContext.schedulerProvider).build().subscribe { transition ->
                var title = ""
                setDefaultSettingsForTools()
                if (transition != null) {
                    title = when (transition.nonNullData) {

                        Transition.ADVERT -> {
                            MobileAds.initialize(questContext,
                                    questContext.getString(R.string.admob_app_id))

                            adView = (questContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                                    as LayoutInflater).inflate(R.layout.sc_advertise_layout, null)
                                    as AdView
                            viewHolder.content.addView(adView)
                            val adRequest = AdRequest.Builder().build()
                            adView.loadAd(adRequest)
                            questContext.getString(R.string.title_advert)
                        }

                        Transition.LEVEL_UP ->

                            questContext.getString(R.string.title_new_level)
                        else -> ""
                    }
                }
                viewHolder.titleView.text = title
            }
        }
    }

    private fun setDefaultSettingsForTools() {}

    override fun onClick(view: View) {
        if (view == viewHolder.okButton) {
            CommitNextTransitionUseCase(questContext.repository,
                    eventSender,
                    questContext.schedulerProvider).use()
        }
    }

    class LockScreenSupportViewContentHolder internal constructor(context: android.content.Context) :
            ViewHolder(context, R.layout.layout_lock_screen_support_view_container), ILockScreenContentViewHolder {

        internal var okButton: Button = view.findViewById<View>(R.id.btn_ok) as Button
        override val contentContainer: View = view.findViewById(R.id.container_content)
        override val titleContentContainer: View = view.findViewById(R.id.container_title)
        internal val content: ViewGroup = view.findViewById(R.id.content) as ViewGroup
        //internal val toolContainer: ViewGroup = view.findViewById(R.id.container_tool) as ViewGroup
        internal val titleView: TextView = view.findViewById(R.id.tv_title) as TextView

        init {
            (view as ViewGroup).removeAllViews()
        }
    }
}