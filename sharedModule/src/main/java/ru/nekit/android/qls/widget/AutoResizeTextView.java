package ru.nekit.android.qls.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.devspark.robototextview.widget.RobotoTextView;

import ru.nekit.android.shared.R;

public class AutoResizeTextView extends RobotoTextView {

    private static final float THRESHOLD = 2f;
    private int minTextSize = 0;
    private int maxTextSize = 0;
    @NonNull
    private Mode mode = Mode.None;
    private boolean inComputation;
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private float threshold;

    public AutoResizeTextView(Context context) {
        super(context);
    }

    public AutoResizeTextView(@NonNull Context context, @NonNull AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray tAttrs = context.obtainStyledAttributes(attrs, R.styleable.AutoResizeTextView, defStyle, 0);
        maxTextSize = tAttrs.getDimensionPixelSize(R.styleable.AutoResizeTextView_maxTextSize, maxTextSize);
        minTextSize = tAttrs.getDimensionPixelSize(R.styleable.AutoResizeTextView_minTextSize, minTextSize);
        threshold = tAttrs.getFloat(R.styleable.AutoResizeTextView_threshold, THRESHOLD);
        tAttrs.recycle();
    }

    private void resizeText() {
        if (getWidth() <= 0 || getHeight() <= 0)
            return;

        if (mode == Mode.None)
            return;

        final int targetWidth = getWidth();
        final int targetHeight = getHeight();

        inComputation = true;
        float higherSize = maxTextSize;
        float lowerSize = minTextSize;
        float textSize = getTextSize();
        while (higherSize - lowerSize > threshold) {
            textSize = (higherSize + lowerSize) / 2;
            if (isTooBig(textSize, targetWidth, targetHeight)) {
                higherSize = textSize;
            } else {
                lowerSize = textSize;
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, lowerSize);
        measure(widthMeasureSpec, heightMeasureSpec);
        inComputation = false;
    }

    private boolean isTooBig(float textSize, int targetWidth, int targetHeight) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        measure(0, 0);
        if (mode == Mode.Both)
            return getMeasuredWidth() >= targetWidth || getMeasuredHeight() >= targetHeight;
        if (mode == Mode.Width)
            return getMeasuredWidth() >= targetWidth;
        else
            return getMeasuredHeight() >= targetHeight;
    }

    @NonNull
    private Mode getMode(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY)
            return Mode.Both;
        if (widthMode == MeasureSpec.EXACTLY)
            return Mode.Width;
        if (heightMode == MeasureSpec.EXACTLY)
            return Mode.Height;
        return Mode.None;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!inComputation) {
            this.widthMeasureSpec = widthMeasureSpec;
            this.heightMeasureSpec = heightMeasureSpec;
            mode = getMode(widthMeasureSpec, heightMeasureSpec);
            resizeText();
        }
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        Drawable background = getBackground();
        setBackground(null);
        int minWidth = super.getSuggestedMinimumWidth();
        setBackground(background);
        return minWidth;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        Drawable background = getBackground();
        setBackground(null);
        int minHeight = super.getSuggestedMinimumHeight();
        setBackground(background);
        return minHeight;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            super.setBackground(background);
        else
            setBackgroundDrawable(background);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        resizeText();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh)
            resizeText();
    }

    public int getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
        resizeText();
    }

    public int getMaxTextSize() {
        return maxTextSize;
    }

    public void setMaxTextSize(int maxTextSize) {
        this.maxTextSize = maxTextSize;
        resizeText();
    }

    private enum Mode {Width, Height, Both, None}
}