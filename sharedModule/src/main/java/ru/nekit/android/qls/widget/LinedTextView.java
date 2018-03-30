package ru.nekit.android.qls.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import ru.nekit.android.shared.R;

public class LinedTextView extends AppCompatTextView {

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
        Rect rect = mRect;
        Paint paint = mPaint;
        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, rect);
            canvas.drawLine(rect.left, baseline + lineWeight, rect.right, baseline + lineWeight, paint);
        }
        super.onDraw(canvas);
    }
}

