package ru.nekit.android.qls.window

import android.content.Context
import android.support.annotation.StringRes
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Single
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.layout.*
import ru.nekit.android.qls.R.string.title_unlock_key_help_on_consume
import ru.nekit.android.qls.R.string.title_unlock_key_help_on_zero_count
import ru.nekit.android.qls.data.representation.getReachRuleRepresentation
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.qls.domain.useCases.ConsumeRewardUseCase
import ru.nekit.android.qls.domain.useCases.SetupWizardUseCases
import ru.nekit.android.qls.lockScreen.mediator.LockScreenContentMediatorAction
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.window.UnlockKeyHelpWindowMediator.Step.HELP_ON_CONSUME
import ru.nekit.android.qls.window.UnlockKeyHelpWindowMediator.Step.HELP_ON_ZERO_COUNT
import ru.nekit.android.qls.window.common.QuestWindowMediator
import ru.nekit.android.utils.RevealPoint
import ru.nekit.android.utils.ViewHolder
import ru.nekit.android.utils.responsiveClicks
import ru.nekit.android.utils.throttleClicks
import ru.nekit.android.window.WindowContentViewHolder

class UnlockKeyHelpWindowMediator private constructor(questContext: QuestContext) :
        QuestWindowMediator(questContext) {

    override val name: String = "unlockKeyHelper"

    private var currentStep: Step? = null
    private var currentContentHolder: ViewHolder? = null
    private lateinit var title: String
    private lateinit var windowContent: UnlockKeyWindowContentViewHolder

    override val windowStyleId: Int = R.style.Window_UnlockKey

    override fun createWindowContent(): Single<WindowContentViewHolder> {
        return Single.fromCallable {
            UnlockKeyWindowContentViewHolder(questContext).also {
                windowContent = it
            }
        }
    }

    private fun switchToContent(contentHolder: ViewHolder) {
        windowContent.titleView.text = title
        windowContent.contentContainer.apply {
            removeAllViews()
            addView(contentHolder.view)
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
                HELP_ON_ZERO_COUNT -> {
                    titleResID = title_unlock_key_help_on_zero_count
                    currentContentHolder = HelpOnZeroCountViewHolder(questContext).also { viewHolder ->
                        autoDispose {
                            viewHolder.okButton.responsiveClicks {
                                closeWindow(RevealPoint.POSITION_BOTTOM_CENTER)
                            }
                        }
                        questContext.getRemainingAmountForReaching {
                            viewHolder.textView.text = ArrayList<String>().apply {
                                add("До получения мистера Ключика:\n")
                                addAll(it.filter {
                                    it.first is Reward.UnlockKey
                                }.map {
                                    val reward = it.first
                                    val count = it.second
                                    reward.getReachRuleRepresentation(questContext, count)
                                })
                            }.joinToString("\n")
                        }
                    }
                }
                HELP_ON_CONSUME -> {
                    titleResID = title_unlock_key_help_on_consume
                    currentContentHolder = HelpOnConsumeViewHolder(questContext)
                    (currentContentHolder as HelpOnConsumeViewHolder).apply {
                        autoDisposeList(step.name,
                                cancelButton.throttleClicks {
                                    closeWindow(RevealPoint.POSITION_BOTTOM_CENTER)
                                },
                                consumeUnlockKeyButton.throttleClicks {
                                    ConsumeRewardUseCase(questContext.repositoryHolder).use(Reward.UnlockKey()) {
                                        if (it) {
                                            closeWindow(RevealPoint.POSITION_MIDDLE_CENTER)
                                            sendEvent(LockScreenContentMediatorAction.CLOSE)
                                        }
                                    }
                                },
                                showNoMore.checkedChanges().subscribe {
                                    SetupWizardUseCases.setShowHelpOnUnlockKeyConsume(!it)
                                }
                        )
                    }
                }
            }
            title = questContext.getString(titleResID)
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
                HELP_ON_ZERO_COUNT -> {
                    (currentContentHolder as HelpOnZeroCountViewHolder).apply {

                    }
                }
                HELP_ON_CONSUME -> {
                    (currentContentHolder as HelpOnConsumeViewHolder).apply {

                    }
                }
            }
            dispose(currentStep!!.name)
        }
        currentStep = null
    }

    enum class Step {

        HELP_ON_ZERO_COUNT,
        HELP_ON_CONSUME;

    }

    internal class UnlockKeyWindowContentViewHolder(context: Context) : WindowContentViewHolder(context, wc_unlock_key) {

        val contentContainer: ViewGroup = view.findViewById(R.id.container_content)
        val titleView: TextView = view.findViewById(R.id.tv_title)
    }

    internal class HelpOnZeroCountViewHolder(context: Context) : ViewHolder(context, wsc_unlock_key_help_on_zero_count) {
        val okButton: Button = view.findViewById(R.id.btn_ok)
        val textView: TextView = view.findViewById(R.id.tv_text)
    }

    internal class HelpOnConsumeViewHolder(context: Context) : ViewHolder(context, wsc_unlock_key_help_on_consume) {

        val cancelButton: Button = view.findViewById(R.id.btn_cancel)
        val consumeUnlockKeyButton: Button = view.findViewById(R.id.btn_consume_unlock_key)
        val showNoMore: CheckBox = view.findViewById(R.id.checker_show_no_more)
    }

    companion object {

        fun openWindow(questContext: QuestContext, step: Step) {
            UnlockKeyHelpWindowMediator(questContext).apply {
                openWindow {
                    setStep(step)
                }
            }
        }
    }
}