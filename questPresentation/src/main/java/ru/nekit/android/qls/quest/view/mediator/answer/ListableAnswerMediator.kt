package ru.nekit.android.qls.quest.view.mediator.answer

import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext

//ver 1.0
abstract class ListableAnswerMediator<ListDataType, ListAdapterType : RecyclerView.Adapter<*>> :
        ButtonListAnswerMediator() {

    protected abstract val listData: List<ListDataType>
    protected lateinit var listAdapter: ListAdapterType
    protected lateinit var listView: RecyclerView
    private lateinit var listViewContainer: RelativeLayout

    protected open val columnCount: Int
        get() = 2

    override val view: View
        get() = listViewContainer

    protected val dataListSize: Int
        get() = listData.size

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        listView = RecyclerView(questContext).apply {
            setHasFixedSize(true)
            overScrollMode = View.OVER_SCROLL_NEVER
            GridLayoutManager(
                    questContext,
                    columnCount,
                    LinearLayoutManager.VERTICAL,
                    false
            ).apply {
                layoutManager = this
            }
            listAdapter = createListAdapter(listData)
            adapter = listAdapter
        }
        listViewContainer = RelativeLayout(questContext).apply {
            addView(listView)
            layoutParams = RelativeLayout.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
            )
            requestLayout()
        }
        with(listView.layoutParams as RelativeLayout.LayoutParams) {
            height = WRAP_CONTENT
            width = WRAP_CONTENT
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }
    }

    @CallSuper
    override fun detachView() {
        listView.adapter = null
        listView.layoutManager = null
        rootContentContainer.removeAllViews()
        super.detachView()
    }

    override fun updateSize() {

    }

    protected fun shuffleListData() {
        listView.apply {
            listAdapter = createListAdapter(listData.shuffled())
            swapAdapter(listAdapter, true)
        }
        updateSize()
    }

    protected abstract fun createListAdapter(listData: List<ListDataType>): ListAdapterType

    override fun onAnswer(answerType: AnswerType): Boolean {
        return true
    }
}