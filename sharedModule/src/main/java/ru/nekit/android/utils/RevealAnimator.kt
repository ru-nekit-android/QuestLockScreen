package ru.nekit.android.utils

import android.animation.Animator
import android.animation.TimeInterpolator
import android.content.Context
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

import io.codetail.animation.ViewAnimationUtils

object RevealAnimator {

    fun getRevealAnimator(context: Context,
                          view: View,
                          position: String,
                          timeInterpolator: TimeInterpolator?,
                          duration: Int,
                          reverse: Boolean): Animator {
        val animator: Animator
        val revealPoint = RevealPoint.getRevealPoint(context, position)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(view,
                    revealPoint.x,
                    revealPoint.y,
                    if (reverse) revealPoint.radius else 0F,
                    if (reverse) 0F else revealPoint.radius)
        } else {
            animator = android.view.ViewAnimationUtils.createCircularReveal(view,
                    revealPoint.x,
                    revealPoint.y,
                    if (reverse) revealPoint.radius else 0F,
                    if (reverse) 0F else revealPoint.radius)
        }
        animator.interpolator = timeInterpolator ?: AccelerateDecelerateInterpolator()
        animator.duration = duration.toLong()
        return animator
    }
}
