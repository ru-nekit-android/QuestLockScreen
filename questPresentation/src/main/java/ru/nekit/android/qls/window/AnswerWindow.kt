package ru.nekit.android.qls.window

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import ru.nekit.android.qls.R
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.utils.ViewHolder
import ru.nekit.android.qls.window.common.QuestWindow

class AnswerWindow private constructor(questContext: QuestContext,
                                       name: String,
                                       contentViewHolder: WindowContentViewHolder,
                                       @StyleRes styleResId: Int) :
        QuestWindow(questContext, name, contentViewHolder, styleResId) {

    enum class Type {

        RIGHT,
        WRONG;

    }

    companion object {

        fun open(questContext: QuestContext,
                 type: AnswerWindow.Type,
                 @StyleRes styleResId: Int,
                 @LayoutRes contentResId: Int,
                 @LayoutRes toolContentResId: Int) {
            AnswerWindow.Builder(questContext, type).setStyle(styleResId).setContent(contentResId).setToolContent(toolContentResId).create().open()
        }

        fun open(questContext: QuestContext,
                 type: AnswerWindow.Type,
                 @StyleRes style: Int,
                 content: ViewHolder,
                 @LayoutRes toolContentResId: Int) {
            AnswerWindow.Builder(questContext, type).setStyle(style).setContent(content).setToolContent(toolContentResId).create().open()
        }
    }


    class Builder(private val questContext: QuestContext, private val mType: Type) {
        private lateinit var window: AnswerWindow
        private var windowContainer: AnswerWindowContainer? = null
        private var contentHolder: ViewHolder? = null
        private var toolContentHolder: ViewHolder? = null
        @StyleRes
        private var styleResId = -1

        fun setContent(content: ViewHolder): Builder {
            contentHolder = content
            return this
        }

        fun setContent(@LayoutRes contentResId: Int): Builder {
            if (contentResId > 0) {
                contentHolder = ViewHolder(questContext, contentResId)
            }
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

        fun create(): AnswerWindow {
            windowContainer = AnswerWindowContainer(questContext)
            if (contentHolder != null) {
                windowContainer!!.contentContainer.addView(contentHolder!!.view, MATCH_PARENT,
                        MATCH_PARENT)
            }
            if (toolContentHolder != null) {
                windowContainer!!.toolContainer.addView(toolContentHolder!!.view)
            }
            window = AnswerWindow(questContext, mType.name, windowContainer!!,
                    if (styleResId == -1)
                        if (mType == Type.RIGHT)
                            R.style.Window_RightAnswer
                        else
                            R.style.Window_WrongAnswer
                    else
                        styleResId)
            return window
        }
    }

    private class AnswerWindowContainer internal constructor(context: Context) : WindowContentViewHolder(context, R.layout.wc_answer) {

        internal val contentContainer: ViewGroup = view.findViewById(R.id.container_content) as ViewGroup
        internal val toolContainer: ViewGroup = view.findViewById(R.id.container_tool) as ViewGroup

        override val closeButtonId
            get() = R.id.btn_ok

    }
}