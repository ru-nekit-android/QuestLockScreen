package ru.nekit.android.qls.lockScreen.window;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.concurrent.CopyOnWriteArrayList;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.AnimationUtils;
import ru.nekit.android.qls.utils.RevealAnimator;
import ru.nekit.android.qls.utils.ScreenHost;

public class Window implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {

    public static final String VALUE_WINDOW_NAME = "ru.nekit.android.qls.value_window_name";
    public static final String EVENT_WINDOW_OPEN = "ru.nekit.android.qls.event_window_open";
    public static final String EVENT_WINDOW_OPENED = "ru.nekit.android.qls.event_window_opened";
    public static final String EVENT_WINDOW_CLOSE = "ru.nekit.android.qls.event_window_close";
    public static final String EVENT_WINDOW_CLOSED = "ru.nekit.android.qls.event_window_closed";

    private static final CopyOnWriteArrayList<Window> windowStack;

    static {
        windowStack = new CopyOnWriteArrayList<>();
    }

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final WindowManager mWindowManager;
    protected WindowContentViewHolder mContent;
    @StyleRes
    protected int mStyleResId;
    @Nullable
    protected String mName;
    private StyleParameters mStyleParameters;
    private WindowViewHolder mViewHolder;
    @Nullable
    private WindowListener mWindowListener;
    private boolean mIsOpen;
    private LayoutParams layoutParams;

    public Window(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mWindowManager = (WindowManager) mQuestContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public Window(@NonNull QuestContext context, @NonNull WindowContentViewHolder content,
                  @StyleRes int styleResId) {
        this(context);
        mContent = content;
        mStyleResId = styleResId;
    }

    public static void closeAllWindows() {
        for (Window window : windowStack) {
            window.close(null);
        }
    }

    public void open(@Nullable String name, @NonNull WindowContentViewHolder content,
                     @StyleRes int styleResId) {
        mName = name;
        mContent = content;
        mStyleResId = styleResId;
        open();
    }


    public void setWindowListener(@NonNull WindowListener windowListener) {
        mWindowListener = windowListener;
    }

    public void open() {
        if (!mIsOpen) {
            mViewHolder = new WindowViewHolder(mQuestContext);
            mStyleParameters = new StyleParameters(mQuestContext, mStyleResId);
            mContent.getCloseButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    close(mStyleParameters.closePosition);
                }
            });
            if (mStyleParameters.openPosition != null) {
                mViewHolder.contentContainer.addOnAttachStateChangeListener(
                        new View.OnAttachStateChangeListener() {
                            @Override
                            public void onViewAttachedToWindow(View view) {
                                view.removeOnAttachStateChangeListener(this);
                                final Animator revealAnimator = RevealAnimator.getRevealAnimator(mQuestContext,
                                        view,
                                        mStyleParameters.openPosition,
                                        new AccelerateInterpolator(),
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
                                        revealAnimator.removeAllListeners();
                                        sendEvent(EVENT_WINDOW_OPENED);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                });
                                revealAnimator.start();
                                AnimationUtils.getColorAnimator(mStyleParameters.backgroundColorStart,
                                        mStyleParameters.backgroundColorEnd,
                                        mStyleParameters.animationDuration,
                                        mViewHolder.contentContainer).start();
                            }

                            @Override
                            public void onViewDetachedFromWindow(View v) {
                            }
                        });
            }
            mViewHolder.contentContainer.addView(mContent.view);
            mViewHolder.view.addOnAttachStateChangeListener(this);
            mIsOpen = true;
            windowStack.add(this);
            mWindowManager.addView(getView(), getLayoutParams());
            if (mWindowListener != null) {
                mWindowListener.onWindowOpen(this);
            }
            sendEvent(EVENT_WINDOW_OPEN);
        }
    }

    public void close() {
        close(mStyleParameters.closePosition);
    }

    private LayoutParams getLayoutParams() {
        layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_DIM_BEHIND
                , PixelFormat.TRANSLUCENT);
        layoutParams.dimAmount = mStyleParameters.dimAmount;
        layoutParams.gravity = Gravity.LEFT;
        return layoutParams;
    }

    public void close(@Nullable String closePosition) {
        if (mIsOpen) {
            mIsOpen = false;
            if (mWindowListener != null) {
                mWindowListener.onWindowClose(this);
            }
            sendEvent(EVENT_WINDOW_CLOSE);
            if (closePosition != null) {
                Animator animator = RevealAnimator.getRevealAnimator(mQuestContext,
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
                        mViewHolder.view.setVisibility(View.INVISIBLE);
                        sendEvent(EVENT_WINDOW_CLOSED);
                        destroy();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
                AnimationUtils.getColorAnimator(mStyleParameters.backgroundColorStart,
                        mStyleParameters.backgroundColorEnd,
                        mStyleParameters.animationDuration,
                        mViewHolder.contentContainer
                ).start();
            } else {
                destroy();
            }
        }
    }

    private void sendEvent(String eventName) {
        mQuestContext.getEventBus().sendEvent(eventName, VALUE_WINDOW_NAME, mName);
    }

    private void destroy() {
        mViewHolder.destroy();
        mContent.getCloseButton().setOnClickListener(null);
        windowStack.remove(this);
        if (getView().getParent() != null) {
            mWindowManager.removeView(getView());
        }
        if (mWindowListener != null) {
            mWindowListener.onWindowClosed(this);
        }
    }

    private View getView() {
        return mViewHolder.view;
    }

    @Override
    public void onViewAttachedToWindow(View view) {
        mViewHolder.view.addOnLayoutChangeListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        mViewHolder.view.removeOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        if (position[1] < getStatusBarHeight()) {
            Point screenSize = ScreenHost.getScreenSize(mQuestContext);
            layoutParams.height = screenSize.y - getStatusBarHeight();
            layoutParams.y = getStatusBarHeight();
            mWindowManager.updateViewLayout(mViewHolder.view, layoutParams);
        }
    }

    private int getStatusBarHeight() {
        return mQuestContext.getResources().getDimensionPixelSize(R.dimen.status_bar_height);
    }

    public interface WindowListener {

        void onWindowOpen(@NonNull Window window);

        void onWindowOpened(@NonNull Window window);

        void onWindowClose(@NonNull Window window);

        void onWindowClosed(@NonNull Window window);

    }

    private static class StyleParameters {

        String openPosition, closePosition;
        @ColorInt
        int backgroundColorStart, backgroundColorEnd;
        @Size
        int animationDuration;
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