package ru.nekit.android.qls.utils;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class AnimationUtils {

    public static ValueAnimator getColorAnimator(@NonNull Context context, @ColorRes int startColor,
                                                 @ColorRes int endColor, int duration,
                                                 @NonNull final View view,
                                                 @NonNull TimeInterpolator interpolator) {
        return getColorAnimator(ContextCompat.getColor(context, startColor),
                ContextCompat.getColor(context, endColor), duration, view, interpolator);
    }

    public static ValueAnimator getColorAnimator(@NonNull Context context, @ColorRes int startColor,
                                                 @ColorRes int endColor, int duration,
                                                 @NonNull final View view) {
        return getColorAnimator(ContextCompat.getColor(context, startColor),
                ContextCompat.getColor(context, endColor), duration, view, null);
    }

    public static ValueAnimator getColorAnimator(@ColorInt int startColor, @ColorInt int endColor,
                                                 int duration, @NonNull final View view) {
        return getColorAnimator(startColor, endColor, duration, view, null);
    }

    public static ValueAnimator getColorAnimator(@ColorInt int startColor, @ColorInt int endColor,
                                                 int duration, @NonNull final View view,
                                                 @Nullable TimeInterpolator interpolator) {
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                startColor, endColor);
        colorAnimation.setDuration(duration);
        if (interpolator != null) {
            colorAnimation.setInterpolator(interpolator);
        }
        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                colorAnimation.removeAllListeners();
                colorAnimation.removeAllUpdateListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        return colorAnimation;
    }
}