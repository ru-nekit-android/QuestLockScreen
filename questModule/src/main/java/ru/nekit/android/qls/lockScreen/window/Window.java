package ru.nekit.android.qls.lockScreen.window;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.RevealAnimator;
import ru.nekit.android.qls.utils.ScreenHost;

public class Window implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {

    public static final String EVENT_WINDOW_OPEN = "ru.nekit.android.qls.event_window_open";
    public static final String EVENT_WINDOW_OPENED = "ru.nekit.android.qls.event_window_opened";
    public static final String EVENT_WINDOW_CLOSE = "ru.nekit.android.qls.event_window_close";
    public static final String EVENT_WINDOW_CLOSED = "ru.nekit.android.qls.event_window_closed";

    private static final CopyOnWriteArrayList<Window> windowStack;

    static {
        windowStack = new CopyOnWriteArrayList<>();
    }

    @NonNull
    private final StyleParameters mStyleParameters;
    @NonNull
    private final WindowContentViewHolder mContent;
    @NonNull
    private final QuestContext mContext;
    @NonNull
    private final WindowViewHolder mViewHolder;
    @NonNull
    private final WindowManager mWindowManager;
    @Nullable
    private WindowListener mWindowListener;
    private boolean mIsOpen;
    private WindowManager.LayoutParams layoutParams;

    public Window(@NonNull QuestContext context,
                  @NonNull WindowContentViewHolder content,
                  @StyleRes int styleResId) {
        mContext = context;
        mContent = content;
        mStyleParameters = new StyleParameters(mContext, styleResId);
        mViewHolder = new WindowViewHolder(mContext);
        mContent.getCloseButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close(true, mStyleParameters.closePosition);
            }
        });
        if (mStyleParameters.openPosition != null) {
            mViewHolder.contentContainer.addOnAttachStateChangeListener(
                    new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View view) {
                            view.removeOnAttachStateChangeListener(this);
                            final Animator revealAnimator = RevealAnimator.getRevealAnimator(mContext,
                                    view,
                                    mStyleParameters.openPosition,
                                    new AnticipateOvershootInterpolator(),
                                    mStyleParameters.animationDuration,
                                    false);
                            revealAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    if (mWindowListener != null) {
                                        mWindowListener.onWindowOpen(Window.this);
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (mWindowListener != null) {
                                        mWindowListener.onWindowOpened(Window.this);
                                    }
                                    //revealAnimator.removeListener(this);
                                    revealAnimator.removeAllListeners();
                                    mContext.getEventBus().sendEvent(EVENT_WINDOW_OPENED);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            revealAnimator.start();
                            getColorAnimator(false).start();
                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                        }
                    });
        }
        mViewHolder.contentContainer.addView(mContent.getView());
        mViewHolder.getView().addOnAttachStateChangeListener(this);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public static Window open(@NonNull QuestContext context,
                              @NonNull WindowContentViewHolder content,
                              @StyleRes int styleResId,
                              @NonNull WindowListener windowListener) {
        Window window = new Window(context, content, styleResId);
        window.setWindowListener(windowListener);
        window.open();
        return window;
    }

    @NonNull
    public static List<Window> getWindowStack() {
        return windowStack;
    }

    public static void closeAllWindows() {
        for (Window window : windowStack) {
            window.close(null);
        }
    }

    private ValueAnimator getColorAnimator(boolean reverse) {
        int startColor = reverse ? mStyleParameters.backgroundColorEnd
                : mStyleParameters.backgroundColorStart;
        int endColor = reverse ? mStyleParameters.backgroundColorStart
                : mStyleParameters.backgroundColorEnd;
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                startColor, endColor);
        colorAnimation.setDuration(mStyleParameters.animationDuration);
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
                mViewHolder.contentContainer.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        return colorAnimation;
    }

    public void setWindowListener(@NonNull WindowListener windowListener) {
        mWindowListener = windowListener;
    }

    public void open() {
        if (!mIsOpen) {
            mIsOpen = true;
            windowStack.add(this);
            mWindowManager.addView(getView(), getLayoutParams());
            if (mWindowListener != null) {
                mWindowListener.onWindowOpen(this);
            }
            mContext.getEventBus().sendEvent(EVENT_WINDOW_OPEN);
        }
    }

    private WindowManager.LayoutParams getLayoutParams() {
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                , PixelFormat.TRANSLUCENT);
        layoutParams.dimAmount = mStyleParameters.dimAmount;
        layoutParams.gravity = Gravity.LEFT;
        return layoutParams;
    }

    public void close(@Nullable String closePosition) {
        close(false, closePosition);
    }

    private void close(final boolean isInternal, @Nullable String closePosition) {
        if (mIsOpen) {
            mIsOpen = false;
            if (mWindowListener != null) {
                mWindowListener.onWindowClose(this, isInternal);
            }
            mContext.getEventBus().sendEvent(EVENT_WINDOW_CLOSE);
            if (closePosition != null) {
                Animator animator = RevealAnimator.getRevealAnimator(mContext,
                        mViewHolder.contentContainer,
                        closePosition,
                        new AnticipateOvershootInterpolator(),
                        mStyleParameters.animationDuration,
                        true);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mViewHolder.getView().setVisibility(View.INVISIBLE);
                        destroy(isInternal);
                        mContext.getEventBus().sendEvent(EVENT_WINDOW_CLOSED);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
                getColorAnimator(true).start();
            } else {
                destroy(isInternal);
            }
        }
    }

    private void destroy(boolean byCloseButton) {
        mViewHolder.destroy();
        mContent.getCloseButton().setOnClickListener(null);
        windowStack.remove(this);
        if (getView().getParent() != null) {
            mWindowManager.removeView(getView());
        }
        if (mWindowListener != null) {
            mWindowListener.onWindowClosed(this, byCloseButton);
        }
    }

    private View getView() {
        return mViewHolder.getView();
    }

    @Override
    public void onViewAttachedToWindow(View view) {
        mViewHolder.getView().addOnLayoutChangeListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        mViewHolder.getView().removeOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        if (position[1] < getStatusBarHeight()) {
            Point screenSize = ScreenHost.getScreenSize(mContext);
            layoutParams.height = screenSize.y - getStatusBarHeight();
            layoutParams.y = getStatusBarHeight();
            mWindowManager.updateViewLayout(mViewHolder.getView(), layoutParams);
        }
    }

    private int getStatusBarHeight() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_height);
    }

    public interface WindowListener {

        void onWindowOpen(@NonNull Window window);

        void onWindowOpened(@NonNull Window window);

        void onWindowClose(@NonNull Window window, boolean internal);

        void onWindowClosed(@NonNull Window window, boolean internal);

    }

    private static class StyleParameters {

        String openPosition, closePosition;
        int backgroundColorStart, backgroundColorEnd, animationDuration;
        float dimAmount;

        StyleParameters(@NonNull Context context, int styleResId) {
            int lockScreenBackgroundColor = ContextCompat.getColor(context,
                    R.color.lock_screen_background_color);
            TypedArray ta = context.obtainStyledAttributes(styleResId, R.styleable.QuestWindowStyle);
            openPosition = ta.getString(R.styleable.QuestWindowStyle_openPosition);
            closePosition = ta.getString(R.styleable.QuestWindowStyle_closePosition);
            backgroundColorStart = ta.getColor(R.styleable.QuestWindowStyle_backgroundColorStart,
                    lockScreenBackgroundColor);
            backgroundColorEnd = ta.getColor(R.styleable.QuestWindowStyle_backgroundColorEnd,
                    lockScreenBackgroundColor);
            animationDuration = ta.getInt(R.styleable.QuestWindowStyle_animationDuration, 0);
            dimAmount = ta.getFloat(R.styleable.QuestWindowStyle_dimAmount, 1f);
            ta.recycle();
        }
    }
}