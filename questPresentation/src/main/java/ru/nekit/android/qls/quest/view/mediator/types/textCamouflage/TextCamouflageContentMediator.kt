package ru.nekit.android.qls.quest.view.mediator.types.textCamouflage

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.types.TextQuest
import ru.nekit.android.qls.quest.view.mediator.content.SimpleContentMediator
import ru.nekit.android.utils.MathUtils
import java.util.*

//ver 1.0
class TextCamouflageContentMediator : SimpleContentMediator() {

    private var dataForGrid: MutableList<String>? = null
    private var textViews: MutableList<TextView>? = null

    private lateinit var _quest: TextQuest

    private var textCamouflageContainer: TextCamouflageContainer? = null

    override val answerInput: EditText?
        get() = null

    override val view: View?
        get() = textCamouflageContainer

    override fun includeInLayout(): Boolean {
        return true
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        _quest = quest as TextQuest
        //viewHolder = new TextCamouflageQuestViewHolder(questContext);
        //viewHolder.textViewGrid.setHasFixedSize(true);
        textCamouflageContainer = TextCamouflageContainer(questContext)
        //((ViewGroup) viewHolder.getView()).addView(textCamouflageContainer);
        var additionSize = 0
        while (additionSize == 0) {
            additionSize = MathUtils.randInt(-1, _quest.questionStringArray!![0].length)
        }
        val gridLayoutManager = GridLayoutManager(questContext,
                _quest.questionStringArray!![0].length + additionSize,
                LinearLayoutManager.VERTICAL, false)
        //viewHolder.textViewGrid.setLayoutManager(gridLayoutManager);
        dataForGrid = ArrayList()
        textViews = ArrayList()
        for (questStringArrayItem in _quest.questionStringArray!!) {
            val charArray = questStringArrayItem.toCharArray()
            for (aChar in charArray) {
                dataForGrid!!.add(aChar.toString().toUpperCase())
                val textView = AppCompatTextView(questContext)
                textView.text = aChar.toString().toUpperCase()
                textView.background = ContextCompat.getDrawable(questContext,
                        R.drawable.background_text_camouflage_label)
                textView.setTextColor(ContextCompat.getColor(questContext, R.color.black))
                textView.gravity = Gravity.CENTER
                textViews!!.add(textView)
                textCamouflageContainer!!.addView(textView)
            }
        }

        //  viewHolder.textViewGrid.setAdapter(new TextCamouflageAdapter(dataForGrid));
    }

    override fun detachView() {
        super.detachView()
        //  viewHolder.textViewGrid.setLayoutManager(null);
        //  viewHolder.textViewGrid.setAdapter(null);
    }

    override fun updateSize() {
        val width = rootContentContainer.width
        var height = rootContentContainer.height
        val savedHeight = height
        height = Math.min(width, height)
        var position = START_POSITION
        var positionIndex = Position.getIndex(position)
        var stepX = 0
        var stepY = 0
        var bounceLeft = 0
        var bounceBottom = 0
        val isVerticalDirection = START_POSITION == Position.TL || START_POSITION == Position.BR
        val textViewWidth = width / textViews!!.size * 2
        val textViewHeight = height / textViews!!.size * 2
        val textViewSize = Math.min(textViewWidth, textViewHeight) - 4
        val pointList = ArrayList<Point>()
        for (i in textViews!!.indices) {
            val textView = textViews!![i]
            if (i != 0) {
                positionIndex = if ((positionIndex + 1) % Position.values().size == 0)
                    0
                else
                    positionIndex + 1
                position = Position.values()[positionIndex]
            }
            if (i != 0) {
                if (START_POSITION == position) {
                    if (isVerticalDirection) {
                        stepY++
                    } else {
                        stepX++
                    }
                }
            }
            val x = if (position == Position.TL || position == Position.BL)
                textViewWidth * stepX
            else
                width - textViewWidth * (stepX + 1)
            val y = if (position == Position.TL || position == Position.TR)
                textViewHeight * stepY
            else
                height - textViewHeight * (stepY + 1)
            val point = Point(x + textViewSize / 2, y + textViewSize / 2)
            pointList.add(point)
            bounceLeft = Math.max(bounceLeft, x + textViewSize)
            bounceBottom = Math.max(bounceBottom, y + textViewSize)
            textView.y = y.toFloat()
            textView.x = x.toFloat()
            if (i != 0) {
                if (START_POSITION == position) {
                    if (isVerticalDirection) {
                        stepX++
                    } else {
                        stepY++
                    }
                }
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (textViewSize / 3).toFloat())
            val textViewLayoutParams = textView.layoutParams
            textViewLayoutParams.width = textViewSize
            textViewLayoutParams.height = textViewSize
        }
        textCamouflageContainer!!.setPointList(pointList)
        textCamouflageContainer!!.x = ((width - bounceLeft) / 2).toFloat()
        textCamouflageContainer!!.y = ((savedHeight - bounceBottom) / 2).toFloat()
        textCamouflageContainer!!.layoutParams.width = width
        textCamouflageContainer!!.layoutParams.height = height
    }

    private enum class Position {
        TL,
        TR,
        BR,
        BL;

        companion object {

            fun getIndex(position: Position): Int {
                val values = values()
                var index = 0
                for (positionItem in values) {
                    if (positionItem == position) {
                        return index
                    }
                    index++
                }
                return index
            }
        }
    }

    internal enum class VisualType {
        SPIRAL,
        SIN
    }

    private inner class TextCamouflageContainer(context: Context) : FrameLayout(context) {

        private lateinit var paint: Paint
        private lateinit var pointList: MutableList<Point>

        init {
            setWillNotDraw(false)
            paint = Paint()
            paint.color = Color.BLACK
            paint.strokeWidth = 10f
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.strokeJoin = Paint.Join.ROUND
        }

        override fun onDetachedFromWindow() {
            pointList.clear()
            super.onDetachedFromWindow()
        }

        fun setPointList(pointList: MutableList<Point>) {
            this.pointList = pointList
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val count = childCount
            (0 until count)
                    .asSequence()
                    .filter { it > 0 }
                    .forEach {
                        canvas.drawLine(pointList[it - 1].x.toFloat(),
                                pointList[it - 1].y.toFloat(),
                                pointList[it].x.toFloat(),
                                pointList[it].y.toFloat(),
                                paint
                        )
                    }
        }
    }

    companion object {

        private val START_POSITION = Position.TL
    }
}
