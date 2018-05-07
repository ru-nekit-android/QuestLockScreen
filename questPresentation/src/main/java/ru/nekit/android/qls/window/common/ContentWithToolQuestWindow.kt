package ru.nekit.android.qls.window.common

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import ru.nekit.android.qls.R
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.utils.ViewHolder
import ru.nekit.android.window.WindowContentViewHolder

class ContentWithToolQuestWindow private constructor(questContext: QuestContext,
                                                     name: String,
                                                     contentViewHolder: WindowContentViewHolder,
                                                     @StyleRes styleResId: Int) :
        QuestWindow(questContext, name, contentViewHolder, styleResId) {


    class Builder(private val questContext: QuestContext,
                  private val name: String) {
        private lateinit var window: ContentWithToolQuestWindow
        private var windowContainer: ContentAndToolWindowContainer? = null
        private var contentHolder: ViewHolder? = null
        private var toolContentHolder: ViewHolder? = null
        @StyleRes
        private var styleResId = -1

        fun setContent(content: ViewHolder): Builder {
            contentHolder = content
            return this
        }

        fun setContent(@LayoutRes contentResId: Int): Builder {
            contentHolder = ViewHolder(questContext, contentResId)
            return this
        }

        fun setToolContent(content: ViewHolder): Builder {
            toolContentHolder = content
            return this
        }

        fun setToolContent(@LayoutRes toolContentResId: Int): Builder {
            toolContentHolder = ViewHolder(questContext, toolContentResId)
            return this
        }

        fun setStyle(@StyleRes styleResId: Int): Builder {
            this.styleResId = styleResId
            return this
        }

        fun create(): ContentWithToolQuestWindow {
            windowContainer = ContentAndToolWindowContainer(questContext)
            if (contentHolder != null) {
                windowContainer!!.contentContainer.addView(contentHolder!!.view, MATCH_PARENT,
                        MATCH_PARENT)
            }
            if (toolContentHolder != null) {
                windowContainer!!.toolContainer.addView(toolContentHolder!!.view)
            }
            window = ContentWithToolQuestWindow(questContext, name, windowContainer!!, styleResId)
            return window
        }
    }

    private class ContentAndToolWindowContainer internal constructor(context: Context) : WindowContentViewHolder(context, R.layout.wc_answer) {

        internal val contentContainer: ViewGroup = view.findViewById(R.id.container_content) as ViewGroup
        internal val toolContainer: ViewGroup = view.findViewById(R.id.container_tool) as ViewGroup

        override val closeButtonId = R.id.btn_ok

    }
}