package ru.nekit.android.qls.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.devspark.robototextview.widget.RobotoTextView;

import ru.nekit.android.qls.R;

public class LinedTextView extends RobotoTextView {

    private Rect mRect;
    private Paint mPaint;

    public LinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xffa1a3a6);
        mPaint.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.line_weight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineWeight = 2 * getContext().getResources().getDimensionPixelOffset(R.dimen.line_weight);
        int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;
        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);
            canvas.drawLine(r.left, baseline + lineWeight, r.right, baseline + lineWeight, paint);
        }
        super.onDraw(canvas);
    }
}

