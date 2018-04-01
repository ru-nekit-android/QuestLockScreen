package ru.nekit.android.utils


import android.animation.*
import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.IntegerRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.view.View
import android.view.ViewPropertyAnimator
import ru.nekit.android.shared.R

object AnimationUtils {

    fun getColorAnimator(context: Context, @ColorRes startColor: Int,
                         @ColorRes endColor: Int, duration: Int,
                         view: View,
                         interpolator: TimeInterpolator): ValueAnimator {
        return getColorAnimator(ContextCompat.getColor(context, startColor),
                ContextCompat.getColor(context, endColor), duration, view, interpolator)
    }

    fun getColorAnimator(context: Context, @ColorRes startColor: Int,
                         @ColorRes endColor: Int, duration: Int,
                         view: View): ValueAnimator {
        return getColorAnimator(ContextCompat.getColor(context, startColor),
                ContextCompat.getColor(context, endColor), duration, view, null)
    }

    fun shake(view: View) {
        ObjectAnimator.ofFloat(view, "rotation", 0f, 20f, 0f, -20f, 0f).apply {
            repeatCount = 5
            duration = 100
            start()
        }
    }

    @JvmOverloads
    fun getColorAnimator(@ColorInt startColor: Int, @ColorInt endColor: Int,
                         duration: Int, view: View,
                         interpolator: TimeInterpolator? = null): ValueAnimator {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(),
                startColor, endColor)
        colorAnimation.duration = duration.toLong()
        if (interpolator != null) {
            colorAnimation.interpolator = interpolator
        }
        colorAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                colorAnimation.removeAllListeners()
                colorAnimation.removeAllUpdateListeners()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        return colorAnimation
    }

    fun fadeAnimation(view: View?, fadeOut: Boolean, duration: Long, actionEnd: () -> Unit) {
        if (view != null) {
            if (!fadeOut) {
                view.alpha = 0f
            }
            view.animate().alpha((if (fadeOut) 0 else 1).toFloat()).setDuration(duration).withEndAction(actionEnd)
        }
    }

    fun fadeAnimation(view: View?, fadeOut: Boolean, duration: Long) = fadeAnimation(view, fadeOut, duration) {}

    fun animate(view: View, duration: Long,
                interpolator: TimeInterpolator,
                startAnimate: (ViewPropertyAnimator) -> ViewPropertyAnimator,
                actionStart: () -> Unit,
                endAnimate: (ViewPropertyAnimator) -> ViewPropertyAnimator,
                actionEnd: () -> Unit): ViewPropertyAnimator =
            view.animate().apply {
                startAnimate(withLayer()
                        .setInterpolator(interpolator)
                        .setDuration(duration)
                        .withEndAction {
                            actionStart()
                            endAnimate(setDuration(duration)
                                    .withEndAction {
                                        view.setLayerType(View.LAYER_TYPE_NONE, null)
                                        actionEnd()
                                    })
                        }
                )
            }

    fun fadeOutAndIn(view: View, duration: Long, actionStart: () -> Unit, actionEnd: () -> Unit) =
            animate(view, duration, FastOutLinearInInterpolator(),
                    { it.alpha(0F) }, actionStart,
                    { it.alpha(1F) }, actionEnd)

    fun fadeOutAndIn(view: View, duration: Long, action: () -> Unit) = fadeOutAndIn(view, duration, action, {})

}

enum class Delay(@IntegerRes private val resourceId: Int) {

    LONG(R.integer.long_animation_duration),
    SHORT(R.integer.short_animation_duration),
    SMALL(R.integer.small_animation_duration),
    KEYBOARD(R.integer.keyboard_delay),
    VIBRATION(R.integer.vibration_delay);

    fun get(context: Context) = context.resources.getInteger(resourceId).toLong()
}