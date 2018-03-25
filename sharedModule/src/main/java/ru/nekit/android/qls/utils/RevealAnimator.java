package ru.nekit.android.qls.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.codetail.animation.ViewAnimationUtils;

public class RevealAnimator {

    public static Animator getRevealAnimator(@NonNull Context context,
                                             @NonNull View view,
                                             @NonNull String position,
                                             @Nullable TimeInterpolator timeInterpolator,
                                             int duration,
                                             boolean reverse) {
        final Animator animator;
        RevealPoint revealPoint = RevealPoint.Companion.getRevealPoint(context, position);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            animator =
                    ViewAnimationUtils.createCircularReveal(view,
                            revealPoint.x,
                            revealPoint.y,
                            reverse ? revealPoint.getRadius() : 0,
                            reverse ? 0 : revealPoint.getRadius());
        } else {
            animator =
                    android.view.ViewAnimationUtils.createCircularReveal(view,
                            revealPoint.x,
                            revealPoint.y,
                            reverse ? revealPoint.getRadius() : 0,
                            reverse ? 0 : revealPoint.getRadius());
        }
        animator.setInterpolator(
                timeInterpolator == null ? new AccelerateDecelerateInterpolator() : timeInterpolator);
        animator.setDuration(duration);
        return animator;
    }
}
