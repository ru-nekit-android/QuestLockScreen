package ru.nekit.android.qls.window

import android.animation.Animator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.Size
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import ru.nekit.android.shared.R
import ru.nekit.android.utils.AnimationUtils
import ru.nekit.android.utils.RevealAnimator
import ru.nekit.android.utils.ScreenHost
import java.util.concurrent.CopyOnWriteArrayList

open class Window(private val context: Context,
                  val name: String,
                  private val windowListener: WindowListener?,
                  private val content: WindowContentViewHolder?,
                  @param:StyleRes @field:StyleRes
                  private val styleResId: Int) : View.OnAttachStateChangeListener, View.OnLayoutChangeListener {

    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var styleParameters: StyleParameters
    private lateinit var viewHolder: WindowViewHolder
    private var isOpen: Boolean = false
    private var layoutParams: LayoutParams? = null

    private val view: View
        get() = viewHolder.view

    private val statusBarHeight: Int
        get() = context.resources.getDimensionPixelSize(R.dimen.status_bar_height)

    fun open() {
        if (!isOpen) {
            viewHolder = WindowViewHolder(context)
            styleParameters = StyleParameters(context, styleResId)
            content!!.closeButton.setOnClickListener({ close(styleParameters.closePosition) })
            if (styleParameters.openPosition != null) {
                viewHolder.contentContainer.addOnAttachStateChangeListener(
                        object : View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(view: View) {
                                view.removeOnAttachStateChangeListener(this)
                                val revealAnimator = RevealAnimator.getRevealAnimator(context,
                                        view,
                                        styleParameters.openPosition!!,
                                        AccelerateInterpolator(),
                                        styleParameters.animationDuration,
                                        false)
                                revealAnimator.addListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {
                                        windowListener!!.onWindowOpen(this@Window)
                                    }

                                    override fun onAnimationEnd(animation: Animator) {
                                        windowListener!!.onWindowOpened(this@Window)
                                        revealAnimator.removeAllListeners()

                                    }

                                    override fun onAnimationCancel(animation: Animator) {}

                                    override fun onAnimationRepeat(animation: Animator) {}
                                })
                                revealAnimator.start()
                                AnimationUtils.getColorAnimator(styleParameters.backgroundColorStart,
                                        styleParameters.backgroundColorEnd,
                                        styleParameters.animationDuration,
                                        viewHolder.contentContainer).start()
                            }

                            override fun onViewDetachedFromWindow(view: View) {}
                        })
            }
            viewHolder.contentContainer.addView(content.view)
            viewHolder.view.addOnAttachStateChangeListener(this)
            isOpen = true
            windowStack.add(this)
            windowManager.addView(view, getLayoutParams())
        }
    }

    private fun getLayoutParams(): LayoutParams {
        layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    @Suppress("DEPRECATION")
                    LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_DIM_BEHIND, PixelFormat.TRANSLUCENT).also {
            it.dimAmount = styleParameters.dimAmount
            it.gravity = Gravity.LEFT
            it.y = statusBarHeight
            it.height = ScreenHost.getScreenSize(context).y - statusBarHeight
        }
        return layoutParams!!
    }

    @JvmOverloads
    fun close(closePosition: String? = styleParameters.closePosition) {
        if (isOpen) {
            isOpen = false
            windowListener!!.onWindowClose(this)
            if (closePosition != null) {
                val animator = RevealAnimator.getRevealAnimator(context,
                        viewHolder.contentContainer,
                        closePosition,
                        AnticipateOvershootInterpolator(),
                        styleParameters.animationDuration,
                        true)
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        viewHolder.view.visibility = View.INVISIBLE
                        destroy()
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                animator.start()
                AnimationUtils.getColorAnimator(styleParameters.backgroundColorStart,
                        styleParameters.backgroundColorEnd,
                        styleParameters.animationDuration,
                        viewHolder.contentContainer
                ).start()
            } else {
                destroy()
            }
        }
    }

    private fun destroy() {
        viewHolder.destroy()
        content!!.closeButton.setOnClickListener(null)
        windowStack.remove(this)
        if (view.parent != null) {
            windowManager.removeView(view)
        }
        windowListener!!.onWindowClosed(this)
    }

    override fun onViewAttachedToWindow(view: View) {
        viewHolder.view.addOnLayoutChangeListener(this)
    }

    override fun onViewDetachedFromWindow(view: View) {
        viewHolder.view.removeOnLayoutChangeListener(this)
    }

    override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                oldTop: Int, oldRight: Int, oldBottom: Int) {
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        if (position[1] < statusBarHeight) {
            val screenSize = ScreenHost.getScreenSize(context)
            layoutParams!!.height = screenSize.y - statusBarHeight
            layoutParams!!.y = statusBarHeight
            windowManager.updateViewLayout(viewHolder.view, layoutParams)
        }
    }

    private class StyleParameters internal constructor(context: android.content.Context, styleResId: Int) {

        internal var openPosition: String? = null
        internal var closePosition: String? = null
        @ColorInt
        internal var backgroundColorStart: Int = 0
        @ColorInt
        internal var backgroundColorEnd: Int = 0
        @Size
        internal var animationDuration: Int = 0
        internal var dimAmount: Float = 0.toFloat()

        init {
            val lockScreenBackgroundColor = ContextCompat.getColor(context,
                    R.color.lock_screen_background_color)
            val ta = context.obtainStyledAttributes(styleResId, R.styleable.WindowStyle)
            openPosition = ta.getString(R.styleable.WindowStyle_openPosition)
            closePosition = ta.getString(R.styleable.WindowStyle_closePosition)
            backgroundColorStart = ta.getColor(R.styleable.WindowStyle_backgroundColorStart,
                    lockScreenBackgroundColor)
            backgroundColorEnd = ta.getColor(R.styleable.WindowStyle_backgroundColorEnd,
                    lockScreenBackgroundColor)
            animationDuration = ta.getInt(R.styleable.WindowStyle_animationDuration, 0)
            dimAmount = ta.getFloat(R.styleable.WindowStyle_dimAmount, 1f)
            ta.recycle()
        }
    }

    companion object {

        private val windowStack: CopyOnWriteArrayList<Window> = CopyOnWriteArrayList()

        fun closeAllWindows() {
            for (window in windowStack) {
                window.close(null)
            }
        }
    }
}