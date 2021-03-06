package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import com.jakewharton.rxbinding2.view.layoutChangeEvents
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.pupil.avatar.IPupilAvatarPart
import ru.nekit.android.qls.pupil.avatar.PupilAvatarConverter
import ru.nekit.android.qls.pupil.avatar.PupilBoyAvatarPart
import ru.nekit.android.qls.pupil.avatar.PupilGirlAvatarPart
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.utils.MathUtils
import ru.nekit.android.utils.ParameterlessSingletonHolder
import ru.nekit.android.utils.responsiveClicks
import java.util.*

class PupilAvatarFragment : QuestSetupWizardFragment() {

    private lateinit var pupilPartVariantCurrentPosition: IntArray
    private lateinit var avatarPartImageList: MutableList<ImageView>
    private lateinit var prevButtonList: MutableList<View>
    private lateinit var nextButtonList: MutableList<View>
    private var pupil: Pupil? = null

    private val avatarParts: Array<IPupilAvatarPart>
        get() = (if (pupil!!.sex == PupilSex.BOY)
            PupilBoyAvatarPart.values()
        else
            PupilGirlAvatarPart.values()) as Array<IPupilAvatarPart>

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_pupil_avatar

    private fun initAvatarViewBuilder(contentContainer: ViewGroup) {
        avatarPartImageList = ArrayList()
        prevButtonList = ArrayList()
        nextButtonList = ArrayList()
        val avatarParts = avatarParts
        val length = avatarParts.size
        pupilPartVariantCurrentPosition = IntArray(length)
        for (avatarPartPosition in 0 until length) {
            pupilPartVariantCurrentPosition[avatarPartPosition] = 0
            val avatarPartImage = ImageView(context)
            val avatarPart = avatarParts[avatarPartPosition]
            avatarPartImage.setImageResource(avatarPart.variants[0])
            //horizontal
            val partImageContainer = RelativeLayout(context)
            val avatarPartImageLayoutParams = RelativeLayout.LayoutParams(MATCH_PARENT,
                    MATCH_PARENT)
            avatarPartImage.layoutParams = avatarPartImageLayoutParams
            val prevButton = ImageButton(context)
            prevButton.setImageResource(R.drawable.ic_keyboard_arrow_left_black_36px)
            prevButton.tag = avatarPartPosition
            autoDispose {
                prevButton.responsiveClicks {
                    updateAvatarPart(getAvatarPartAndVariantPositionList(prevButton.tag as Int, false))
                }
            }
            val nextButton = ImageButton(context)
            nextButton.tag = avatarPartPosition
            autoDispose {
                nextButton.responsiveClicks {
                    updateAvatarPart(getAvatarPartAndVariantPositionList(nextButton.tag as Int, true))
                }
            }
            nextButton.setImageResource(R.drawable.ic_keyboard_arrow_right_black_36px)
            val partImageContainerLayoutParams = RelativeLayout.LayoutParams(MATCH_PARENT,
                    MATCH_PARENT)
            partImageContainer.layoutParams = partImageContainerLayoutParams
            val prevButtonContainer = FrameLayout(context)
            val prevButtonContainerLayoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            prevButtonContainer.layoutParams = prevButtonContainerLayoutParams
            val prevButtonLayoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            prevButtonContainer.layoutParams = prevButtonContainerLayoutParams
            prevButton.layoutParams = prevButtonLayoutParams
            prevButtonContainer.addView(prevButton)
            val nextButtonContainer = FrameLayout(context)
            val nextButtonContainerLayoutParams = RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            nextButtonContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            nextButtonContainer.layoutParams = nextButtonContainerLayoutParams
            val nextButtonLayoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            nextButton.layoutParams = nextButtonLayoutParams
            nextButtonContainer.addView(nextButton)
            partImageContainer.addView(prevButtonContainer)
            partImageContainer.addView(nextButtonContainer)
            partImageContainer.addView(avatarPartImage)
            contentContainer.addView(partImageContainer)
            partImageContainer.requestLayout()
            avatarPartImageList.add(avatarPartImage)
            prevButtonList.add(prevButton)
            nextButtonList.add(nextButton)
        }
        randomAvatar()
    }

    override fun onSetupStart(view: View) {
        autoDisposeList(
                setupWizard.pupil.subscribe { it ->
                    pupil = it.nonNullData
                    initAvatarViewBuilder(view.findViewById(R.id.container_content))
                },
                view.layoutChangeEvents()
                        .subscribe {
                            onLayoutChange()
                        }
        )
        title = R.string.title_pupil_avatar
        altButtonText(R.string.label_set_random_avatar)
    }

    override fun nextAction(): Single<Boolean> =
            setupWizard.setPupilAvatar(PupilAvatarConverter.toString(avatarParts,
                    pupilPartVariantCurrentPosition))

    private fun onLayoutChange() {
        pupil?.let { pupil ->
            for ((avatarPartPosition, imageAvatarPart) in avatarPartImageList.withIndex()) {
                val scale = imageAvatarPart.height.toFloat() / resources.getDimensionPixelSize(
                        if (pupil.sex == PupilSex.BOY)
                            R.dimen.boy_avatar_height
                        else
                            R.dimen.girl_avatar_height)
                val avatarPartTopPosition = avatarParts[avatarPartPosition].y
                val prevButton = prevButtonList[avatarPartPosition]
                val nextButton = nextButtonList[avatarPartPosition]
                if (avatarPartTopPosition != 0) {
                    val y = resources.getDimensionPixelSize(avatarPartTopPosition) * scale
                    prevButton.y = y
                    nextButton.y = y
                } else {
                    prevButton.visibility = GONE
                    nextButton.visibility = GONE
                }
            }
        }
    }

    private fun getAvatarPartAndVariantPositionList(avatarPartPosition: Int,
                                                    increment: Boolean): List<Pair<Int, Int>> {
        val avatarPartVariantPositionList = ArrayList<Pair<Int, Int>>()
        var avatarPartVariantPosition = pupilPartVariantCurrentPosition[avatarPartPosition]
        if (increment) {
            avatarPartVariantPosition++
        } else {
            avatarPartVariantPosition--
        }
        val avatarPart = avatarParts[avatarPartPosition]
        val dependentList = avatarPart.dependentItems
        if (dependentList != null) {
            for (dependentItem in dependentList) {
                var avatarPartPositionLocal = 0
                for (searchingItem in avatarParts) {
                    if (searchingItem == dependentItem) {
                        break
                    }
                    avatarPartPositionLocal++
                }
                avatarPartVariantPositionList.addAll(getAvatarPartAndVariantPositionList(
                        avatarPartPositionLocal,
                        increment))
            }
        }
        if (increment) {
            if (avatarPartVariantPosition >= avatarPart.variants.size)
                avatarPartVariantPosition = 0
        } else {
            if (avatarPartVariantPosition < 0)
                avatarPartVariantPosition = avatarPart.variants.size - 1
        }
        avatarPartVariantPositionList.add(Pair(avatarPartPosition, avatarPartVariantPosition))
        return avatarPartVariantPositionList
    }

    private fun updateAvatarPart(avatarPartAndVariantPositionList: List<Pair<Int, Int>>) {
        for (avatarPartAndVariantPositionItem in avatarPartAndVariantPositionList) {
            val avatarPart = avatarParts[avatarPartAndVariantPositionItem.first]
            pupilPartVariantCurrentPosition[avatarPartAndVariantPositionItem.first] = avatarPartAndVariantPositionItem.second
            @DrawableRes val partId = avatarPart.variants[avatarPartAndVariantPositionItem.second]
            avatarPartImageList[avatarPartAndVariantPositionItem.first].setImageResource(partId)
        }
    }

    override fun altAction() = randomAvatar()

    private fun randomAvatar() {
        val avatarParts = avatarParts
        val length = avatarParts.size
        val resultVariantPositions = IntArray(length)
        for (avatarPartPosition in 0 until length) {
            resultVariantPositions[avatarPartPosition] = -1
        }
        for (avatarPartPosition in 0 until length) {
            if (resultVariantPositions[avatarPartPosition] == -1) {
                val avatarPart = avatarParts[avatarPartPosition]
                val dependentList = avatarPart.dependentItems
                val variantPosition = MathUtils.randUnsignedInt(avatarPart.variants.size - 1)
                resultVariantPositions[avatarPartPosition] = variantPosition
                if (dependentList != null) {
                    for (dependentItem in dependentList) {
                        var avatarPartPositionLocal = 0
                        for (searchingItem in avatarParts) {
                            if (searchingItem == dependentItem) {
                                break
                            }
                            avatarPartPositionLocal++
                        }
                        resultVariantPositions[avatarPartPositionLocal] = variantPosition
                    }
                } else {
                    resultVariantPositions[avatarPartPosition] = variantPosition
                }
            }
        }
        for (avatarPartPosition in 0 until length) {
            val pair = Pair(avatarPartPosition,
                    resultVariantPositions[avatarPartPosition])
            val pairList = ArrayList<Pair<Int, Int>>()
            pairList.add(pair)
            updateAvatarPart(pairList)
        }
    }

    companion object : ParameterlessSingletonHolder<PupilAvatarFragment>(::PupilAvatarFragment)

}