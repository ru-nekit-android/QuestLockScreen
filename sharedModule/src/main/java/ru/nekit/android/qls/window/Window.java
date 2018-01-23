package ru.nekit.android.qls.window;

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

import ru.nekit.android.qls.utils.AnimationUtils;
import ru.nekit.android.qls.utils.RevealAnimator;
import ru.nekit.android.qls.utils.ScreenHost;
import ru.nekit.android.shared.R;

public class Window implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {

    private static final CopyOnWriteArrayList<Window> windowStack;

    static {
        windowStack = new CopyOnWriteArrayList<>();
    }

    @NonNull
    protected final Context mContext;
    @NonNull
    private final WindowManager mWindowManager;
    @Nullable
    private final WindowListener mWindowListener;
    WindowContentViewHolder mContent;
    @StyleRes
    int mStyleResId;
    @Nullable
    private String mName;
    private StyleParameters mStyleParameters;
    private WindowViewHolder mViewHolder;
    private boolean mIsOpen;
    private LayoutParams layoutParams;

    public Window(@NonNull Context context,
                  @Nullable String name,
                  @Nullable WindowListener windowListener,
                  @Nullable WindowContentViewHolder content,
                  @StyleRes int styleResId) {
        mContext = context;
        mName = name;
        mWindowListener = windowListener;
        mContent = content;
        mStyleResId = styleResId;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static void closeAllWindows() {
        for (Window window : windowStack) {
            window.close(null);
        }
    }

    public void open() {
        if (!mIsOpen) {
            mViewHolder = new WindowViewHolder(mContext);
            mStyleParameters = new StyleParameters(mContext, mStyleResId);
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
                                final Animator revealAnimator = RevealAnimator.getRevealAnimator(mContext,
                                        view,
                                        mStyleParameters.openPosition,
                                        new AccelerateInterpolator(),
                                        mStyleParameters.animationDuration,
                                        false);
                                revealAnimator.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        mWindowListener.onWindowOpen(Window.this);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mWindowListener.onWindowOpened(Window.this);
                                        revealAnimator.removeAllListeners();

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
                            public void onViewDetachedFromWindow(View view) {
                            }
                        });
            }
            mViewHolder.contentContainer.addView(mContent.view);
            mViewHolder.view.addOnAttachStateChangeListener(this);
            mIsOpen = true;
            windowStack.add(this);
            mWindowManager.addView(getView(), getLayoutParams());
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
        layoutParams.y = getStatusBarHeight();
        layoutParams.height = ScreenHost.getScreenSize(mContext).y - getStatusBarHeight();
        return layoutParams;
    }

    public void close(@Nullable String closePosition) {
        if (mIsOpen) {
            mIsOpen = false;
            mWindowListener.onWindowClose(this);
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
                        mViewHolder.view.setVisibility(View.INVISIBLE);
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


    private void destroy() {
        mViewHolder.destroy();
        mContent.getCloseButton().setOnClickListener(null);
        windowStack.remove(this);
        if (getView().getParent() != null) {
            mWindowManager.removeView(getView());
        }
        mWindowListener.onWindowClosed(this);
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
            Point screenSize = ScreenHost.getScreenSize(mContext);
            layoutParams.height = screenSize.y - getStatusBarHeight();
            layoutParams.y = getStatusBarHeight();
            mWindowManager.updateViewLayout(mViewHolder.view, layoutParams);
        }
    }

    @NonNull
    public String getName() {
        return mName;
    }

    private int getStatusBarHeight() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_height);
    }

    private static class StyleParameters {

        String openPosition, closePosition;
        @ColorInt
        int backgroundColorStart, backgroundColorEnd;
        @Size
        int animationDuration;
        float dimAmount;

        StyleParameters(@NonNull android.content.Context context, int styleResId) {
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