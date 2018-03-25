package ru.nekit.android.qls.quest.view.mediator.types.fruitArithmetic

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.AppCompatImageView
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.FruitArithmeticQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection.*
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.resources.representation.getDrawableRepresentation
import ru.nekit.android.qls.quest.view.mediator.content.SimpleContentMediator
import ru.nekit.android.qls.shared.model.QuestionType.SOLUTION

//ver 1.0
class FruitArithmeticQuestContentMediator : SimpleContentMediator() {

    private lateinit var contentContainer: LinearLayout
    private lateinit var fruitImageDrawableCache: SparseArray<Drawable>
    private lateinit var fruitShadowImageDrawableCache: SparseArray<Drawable>
    private lateinit var mQuest: FruitArithmeticQuest

    override val view: View?
        get() = contentContainer

    override val answerInput: EditText?
        get() = null

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        fruitImageDrawableCache = SparseArray()
        fruitShadowImageDrawableCache = SparseArray()
        mQuest = quest as FruitArithmeticQuest
        with(mQuest) {
            if (questionType == SOLUTION) {
                contentContainer = LinearLayout(questContext)
                val length = visualRepresentationList.size
                for (i in 0 until length) {
                    contentContainer.addView(createFruitView(questContext,
                            visualRepresentationList[i]))
                }
                val contentLayoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
                contentContainer.orientation = LinearLayout.HORIZONTAL
                contentContainer.gravity = Gravity.CENTER_VERTICAL
                contentContainer.layoutParams = contentLayoutParams
            }
        }
    }

    override fun deactivate() {
        fruitImageDrawableCache.clear()
        fruitShadowImageDrawableCache.clear()
        super.deactivate()
    }

    override fun detachView() {
        contentContainer.removeAllViews()
    }

    private fun getFruitImageDrawable(questContext: QuestContext,
                                      visualRepresentationId: Int): Drawable {
        return ContextCompat.getDrawable(questContext,
                questContext.questResourceRepository.getVisualResourceItemById(visualRepresentationId)
                        .getDrawableRepresentation().drawableResourceId)!!
    }

    private fun createFruitView(questContext: QuestContext,
                                visualRepresentationId: Int): View {
        val fruitImageView = AppCompatImageView(questContext)
        val fruitShadowImageView = AppCompatImageView(questContext)
        fruitImageView.tag = visualRepresentationId
        var fruitImageDrawable: Drawable? = fruitImageDrawableCache.get(visualRepresentationId)
        if (fruitImageDrawable == null) {
            fruitImageDrawable = getFruitImageDrawable(questContext, visualRepresentationId)
            fruitImageDrawableCache.append(visualRepresentationId, fruitImageDrawable)
        }
        fruitImageView.setImageDrawable(fruitImageDrawable)
        var fruitShadowImageDrawable: Drawable? = fruitShadowImageDrawableCache.get(visualRepresentationId)
        if (fruitShadowImageDrawable == null) {
            val csl = AppCompatResources.getColorStateList(questContext, R.color.semi_black)
            fruitShadowImageDrawable = DrawableCompat.wrap(getFruitImageDrawable(questContext,
                    visualRepresentationId).mutate())
            DrawableCompat.setTintList(fruitShadowImageDrawable!!, csl)
            fruitShadowImageDrawableCache.append(visualRepresentationId, fruitShadowImageDrawable)
        }
        val fruitContainer = FrameLayout(questContext)
        fruitShadowImageView.setImageDrawable(fruitShadowImageDrawable)
        fruitShadowImageView.y = questContext.resources.getDimensionPixelSize(R.dimen.fruit_shadow_height).toFloat()
        fruitContainer.addView(fruitShadowImageView)
        fruitImageView.x = questContext.resources.getDimensionPixelSize(R.dimen.fruit_shadow_width).toFloat()
        fruitContainer.addView(fruitImageView)
        return fruitContainer
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateSizeInternal()
    }

    override fun onQuestStart(delayedPlay: Boolean) {
        super.onQuestStart(delayedPlay)
        if (!delayedPlay) {
            updateSizeInternal()
        }
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        if (delayedPlay) {
            updateSizeInternal()
        }
        super.onQuestPlay(delayedPlay)
    }

    override fun updateSize() {
        //none
    }

    private fun updateSizeInternal() {
        with(mQuest) {
            if (questionType == SOLUTION) {
                val length = visualRepresentationList.size
                var index = 0
                for (i in 0 until length) {
                    val visualRepresentationItemView = contentContainer.getChildAt(i)
                    val baseSize = view!!.width / length
                    val visualRepresentationId = visualRepresentationList[i]
                    var visualRepresentationIdNext = visualRepresentationId
                    if (i < length - 1) {
                        visualRepresentationIdNext = visualRepresentationList[i + 1]
                    }
                    val visualRepresentationItemLayoutParams = LayoutParams(0, MATCH_PARENT, 1f)
                    var marginRight = (-baseSize / 2).toFloat()
                    var marginLeft = 0f
                    var marginTop: Float
                    index++
                    if (visualRepresentationIdNext != visualRepresentationId) {
                        marginRight = 0f
                        index = 0
                    }
                    marginTop = (Math.min(2, index) * baseSize / 4).toFloat()
                    with(questContext.questResourceRepository) {
                        if (visualRepresentationId == getVisualResourceItemId(MINUS)
                                || visualRepresentationId == getVisualResourceItemId(PLUS)
                                || visualRepresentationId == getVisualResourceItemId(EQUAL)) {
                            marginRight = 0f
                            marginLeft = 0f
                            marginTop = 0f
                        }
                    }
                    if (index > 0) {
                        visualRepresentationItemLayoutParams.setMargins(
                                marginLeft.toInt(),
                                marginTop.toInt(),
                                marginRight.toInt(),
                                0
                        )
                    }
                    visualRepresentationItemView.layoutParams = visualRepresentationItemLayoutParams
                    visualRepresentationItemView.requestLayout()
                }
            }
        }
    }

    override fun includeInLayout(): Boolean {
        return true
    }
}