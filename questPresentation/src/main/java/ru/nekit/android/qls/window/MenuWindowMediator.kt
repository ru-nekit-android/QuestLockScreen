package ru.nekit.android.qls.window

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.*
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import io.reactivex.Single
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.domain.useCases.CheckUnlockSecretUseCase
import ru.nekit.android.qls.domain.useCases.PhoneContactsUseCases
import ru.nekit.android.qls.lockScreen.mediator.LockScreenMediatorAction
import ru.nekit.android.qls.lockScreen.service.OutgoingCall
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.providers.IEventSenderProvider
import ru.nekit.android.qls.setupWizard.BaseSetupWizard
import ru.nekit.android.qls.utils.PhoneUtils
import ru.nekit.android.qls.utils.Vibrate
import ru.nekit.android.qls.view.adapters.PhoneContactListener
import ru.nekit.android.qls.view.adapters.PhoneContactsAdapterForReading
import ru.nekit.android.qls.window.MenuWindowMediator.Step.*
import ru.nekit.android.qls.window.common.QuestWindowMediator
import ru.nekit.android.utils.*
import ru.nekit.android.window.WindowContentViewHolder
import java.util.*

class MenuWindowMediator private constructor(questContext: QuestContext) :
        QuestWindowMediator(questContext),
        PhoneContactListener,
        PatternLockViewListener,
        IEventSenderProvider {

    override val eventSender: IEventSender
        get() = questContext.eventSender

    private var currentStep: Step? = null
    private var currentContentHolder: ViewHolder? = null
    private lateinit var title: String
    private lateinit var windowContent: MenuWindowContentViewHolder
    private lateinit var phoneContacts: List<PhoneContact>

    override val windowStyleId: Int = R.style.Window_Menu

    private val application: QuestLockScreenApplication
        get() = questContext.application

    override fun createWindowContent(): Single<WindowContentViewHolder> {
        return getPhoneContacts().map {
            phoneContacts = it
            val phoneIsAvailable = PhoneUtils.phoneIsAvailable(questContext) && it.isNotEmpty()
            MenuWindowContentViewHolder(questContext).let {
                windowContent = it
                val margin = questContext.resources.getDimensionPixelOffset(R.dimen.normal_gap)
                values().forEach { step ->
                    AppCompatImageButton(questContext).apply {
                        tag = step
                        setImageResource(icons[step]!![1])
                        LinearLayout.LayoutParams(0,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                            setMargins(margin, margin, margin, margin)
                            layoutParams = this
                        }
                        windowContent.buttonContainer.addView(this)
                        autoDispose {
                            throttleClicks {
                                setStep(step)
                            }
                        }
                        if (step == PHONE)
                            visibility = if (phoneIsAvailable) VISIBLE else GONE
                    }
                }
                setStep(if (phoneIsAvailable) PHONE else Step.getByOrdinal(PHONE.ordinal + 1))
                val notify = (0 until windowContent.buttonContainer.childCount).count {
                    windowContent.buttonContainer.getChildAt(it).visibility == VISIBLE
                }
                windowContent.buttonContainer.visibility = if (notify == 1) INVISIBLE else VISIBLE
                windowContent
            }
        }
    }

    private fun switchToContent(contentHolder: ViewHolder) {
        AnimationUtils.fadeOutAndIn(windowContent.contentContainer,
                Delay.SMALL.get(questContext))
        {
            windowContent.contentContainer.removeAllViews()
            windowContent.contentContainer.addView(contentHolder.view)
        }
        AnimationUtils.fadeOutAndIn(windowContent.titleView,
                Delay.SMALL.get(questContext))
        {
            windowContent.titleView.text = title
        }
    }

    private fun setStep(step: Step) {
        if (currentStep != step) {
            @StringRes var titleResID = 0
            if (currentStep != null) {
                destroyContentForStep()
            }
            currentStep = step
            when (step) {

                PHONE -> {
                    titleResID = R.string.title_phone
                    currentContentHolder = PhoneViewHolder(questContext)
                    (currentContentHolder as PhoneViewHolder).let { phoneViewHolder ->
                        val phoneContactsAdapter = PhoneContactsAdapterForReading(phoneContacts,
                                this)
                        val linearLayoutManager = LinearLayoutManager(questContext)
                        phoneViewHolder.contactsListView.adapter = phoneContactsAdapter
                        phoneViewHolder.contactsListView.layoutManager = linearLayoutManager
                    }
                }

                UNLOCK -> {
                    titleResID = R.string.title_unlock_secret
                    currentContentHolder = UnlockViewHolder(questContext)
                    (currentContentHolder as UnlockViewHolder).apply {
                        patterLockView.addPatternLockListener(this@MenuWindowMediator)
                    }
                }
            }
            title = questContext.getString(titleResID)
            (0 until windowContent.buttonContainer.childCount).forEach { i ->
                val button = windowContent.buttonContainer.getChildAt(i) as AppCompatImageButton
                val stepOfButton = button.tag as Step
                button.setImageResource(icons[stepOfButton]!![(if (stepOfButton == step) 1 else 0)])
            }
            switchToContent(currentContentHolder!!)
        }
    }

    override fun destroy() {
        destroyContentForStep()
        super.destroy()
    }

    private fun destroyContentForStep() {
        if (currentStep != null) {
            when (currentStep) {
                PHONE -> (currentContentHolder as PhoneViewHolder).apply {
                    with(contactsListView) {
                        adapter = null
                        layoutManager = null
                    }
                }
                UNLOCK -> (currentContentHolder as UnlockViewHolder).apply {
                    patterLockView.removePatternLockListener(this@MenuWindowMediator)
                }
            }
        }
        currentStep = null
    }

    private fun getPhoneContacts(): Single<List<PhoneContact>> = PhoneContactsUseCases.getPhoneContacts().map { it.toList() }

    override fun onAction(position: Int) {
        sendEvent(OutgoingCall(phoneContacts[position].contactId))
        closeWindow(RevealPoint.POSITION_BOTTOM_CENTER)
    }

    override fun onStarted() {

    }

    override fun onProgress(progressPattern: List<PatternLockView.Dot>) {

    }

    override fun onComplete(pattern: List<PatternLockView.Dot>) {
        val unlockViewHolder = currentContentHolder as UnlockViewHolder
        if (pattern.size >= BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE) {
            CheckUnlockSecretUseCase(application,
                    application.getTimeProvider(),
                    application.getDefaultSchedulerProvider()).use(
                    PatternLockUtils.patternToMD5(unlockViewHolder.patterLockView, pattern)) { result ->
                if (result) {
                    closeWindow(RevealPoint.POSITION_MIDDLE_CENTER)
                    eventSender.send(LockScreenMediatorAction.CLOSE)
                    unlockViewHolder.patterLockView.clearPattern()
                } else {
                    unlockViewHolder.patterLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                    Vibrate.make(questContext, 400)
                }
            }
        } else {
            unlockViewHolder.patterLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
            Vibrate.make(questContext, 400)
        }
    }

    override fun onCleared() {

    }

    enum class Step {

        PHONE,
        UNLOCK;

        companion object {

            fun getByOrdinal(ordinal: Int): Step {
                return values()[ordinal]
            }
        }
    }

    internal class MenuWindowContentViewHolder(context: Context) : WindowContentViewHolder(context, R.layout.wc_menu) {

        val contentContainer: ViewGroup = view.findViewById(R.id.container_content) as ViewGroup
        val buttonContainer: ViewGroup = view.findViewById(R.id.container_button) as ViewGroup
        val titleView: TextView = view.findViewById(R.id.tv_title) as TextView

    }

    internal class PhoneViewHolder(context: Context) : ViewHolder(context, R.layout.wsc_phone) {
        val contactsListView: RecyclerView = view.findViewById(R.id.list_phone_contacts) as RecyclerView
    }

    internal class UnlockViewHolder(context: Context) : ViewHolder(context, R.layout.wsc_unlock) {
        var patterLockView: PatternLockView = view.findViewById(R.id.unlock_secret_view) as PatternLockView
    }

    companion object {

        private val icons = HashMap<Step, List<Int>>()

        init {
            icons[PHONE] = listOf(R.drawable.ic_phone_24dp, R.drawable.ic_phone_36dp)
            icons[UNLOCK] = listOf(R.drawable.ic_lock_open_24dp, R.drawable.ic_lock_open_36dp)
        }

        fun openWindow(questContext: QuestContext) {
            MenuWindowMediator(questContext).apply {
                openWindow {}
            }
        }
    }
}