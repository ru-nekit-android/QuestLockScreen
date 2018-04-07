package ru.nekit.android.qls.window

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.view.adapters.StatisticsAdapter
import ru.nekit.android.qls.window.StatisticsWindowMediator.Step.*
import ru.nekit.android.qls.window.common.QuestWindowMediator
import ru.nekit.android.utils.AnimationUtils
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.ViewHolder
import ru.nekit.android.utils.throttleClicks
import ru.nekit.android.window.WindowContentViewHolder
import java.util.*

class StatisticsWindowMediator private constructor(questContext: QuestContext) :
        QuestWindowMediator(questContext) {

    private var currentStep: Step? = null
    private var currentContentHolder: ViewHolder? = null
    private lateinit var title: String
    private lateinit var windowContent: StatisticsWindowContentViewHolder

    override val windowStyleId: Int = R.style.Window_Statistics

    override fun createWindowContent(): Single<WindowContentViewHolder> {
        return Single.fromCallable {
            StatisticsWindowContentViewHolder(questContext).let {
                windowContent = it
                val margin = questContext.resources.getDimensionPixelOffset(R.dimen.normal_gap)
                values().forEach { step ->
                    AppCompatImageButton(questContext).apply {
                        tag = step
                        setImageResource(icons[step]!![1])
                        val params = LinearLayout.LayoutParams(0,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        params.setMargins(margin, margin, margin, margin)
                        layoutParams = params
                        windowContent.buttonContainer.addView(this)
                        autoDispose {
                            throttleClicks {
                                setStep(step)
                            }
                        }
                    }
                }
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
                Delay.SMALL.get(questContext)) {
            windowContent.contentContainer.removeAllViews()
            windowContent.contentContainer.addView(contentHolder.view)
        }
        AnimationUtils.fadeOutAndIn(windowContent.titleView,
                Delay.SMALL.get(questContext)) {
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
                STATISTICS -> {
                    titleResID = R.string.title_statistics
                    currentContentHolder = StatisticsViewHolder(questContext)
                    (currentContentHolder as StatisticsViewHolder).let { viewHolder ->
                        questStatisticsReport { }
                        statistics {
                            val statisticsAdapter = StatisticsAdapter(it)
                            val linearLayoutManager = LinearLayoutManager(questContext)
                            viewHolder.statisticsListView.adapter = statisticsAdapter
                            viewHolder.statisticsListView.layoutManager = linearLayoutManager
                        }
                    }
                }
                REWARDS -> {
                    titleResID = R.string.title_rewards
                    currentContentHolder = RewardViewHolder(questContext)
                    (currentContentHolder as RewardViewHolder).apply {

                    }
                }
                GIFTS -> {
                    titleResID = R.string.title_gifts
                    currentContentHolder = GiftsViewHolder(questContext)
                    (currentContentHolder as GiftsViewHolder).apply {

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

                STATISTICS -> {
                    (currentContentHolder as StatisticsViewHolder).apply {
                        statisticsListView.adapter = null
                        statisticsListView.layoutManager = null
                    }
                }

                REWARDS -> {
                    (currentContentHolder as RewardViewHolder).apply {
                        rewardsListView.adapter = null
                        rewardsListView.layoutManager = null
                    }
                }

                GIFTS -> {
                    (currentContentHolder as GiftsViewHolder).apply {
                        giftsListView.adapter = null
                        giftsListView.layoutManager = null
                    }
                }
            }
        }
        currentStep = null
    }

    enum class Step {

        STATISTICS,
        REWARDS,
        GIFTS;

        companion object {

            fun getByOrdinal(ordinal: Int): Step = values()[ordinal]
        }
    }

    internal class StatisticsWindowContentViewHolder(context: Context) : WindowContentViewHolder(context, R.layout.wc_menu) {

        val contentContainer: ViewGroup = view.findViewById(R.id.container_content) as ViewGroup
        val buttonContainer: ViewGroup = view.findViewById(R.id.container_button) as ViewGroup
        val titleView: TextView = view.findViewById(R.id.tv_title) as TextView

    }

    internal class StatisticsViewHolder(context: Context) : ViewHolder(context, R.layout.wsc_statistics) {
        val statisticsListView: RecyclerView = view.findViewById(R.id.list_statistics) as RecyclerView
    }

    internal class RewardViewHolder(context: Context) : ViewHolder(context, R.layout.wsc_rewards) {
        var rewardsListView: RecyclerView = view.findViewById(R.id.list_rewards) as RecyclerView
    }

    internal class GiftsViewHolder(context: Context) : ViewHolder(context, R.layout.wsc_gifts) {
        var giftsListView: RecyclerView = view.findViewById(R.id.list_gifts) as RecyclerView
    }

    companion object {

        private val icons = HashMap<Step, List<Int>>()

        init {
            icons[STATISTICS] = listOf(R.drawable.ic_account_24dp, R.drawable.ic_account_36dp)
            icons[REWARDS] = listOf(R.drawable.ic_account_24dp, R.drawable.ic_account_36dp)
            icons[GIFTS] = listOf(R.drawable.ic_account_24dp, R.drawable.ic_account_36dp)
        }

        fun openWindow(questContext: QuestContext) {
            StatisticsWindowMediator(questContext).apply {
                openWindow {
                    setStep(STATISTICS)
                }
            }
        }
    }
}