package ru.nekit.android.qls.quest.mediator.textCamouflage;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.shared.content.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.types.quest.TextQuest;
import ru.nekit.android.qls.utils.MathUtils;

public class TextCamouflageContentMediator extends AbstractQuestContentMediator {

    private static final Position START_POSITION = Position.TL;

    private List<String> dataForGrid;
    private List<TextView> mTextViews;

    private TextCamouflageContainer mTextCamouflageContainer;

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }

    @Override
    public void onCreateQuest(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.onCreateQuest(questContext, rootContentContainer);
        //mViewHolder = new TextCamouflageQuestViewHolder(questContext);
        TextQuest quest = (TextQuest) mQuest;
        // mViewHolder.textViewGrid.setHasFixedSize(true);
        mTextCamouflageContainer = new TextCamouflageContainer(mQuestContext);
        // ((ViewGroup) mViewHolder.getView()).addView(mTextCamouflageContainer);
        int additionSize = 0;
        while (additionSize == 0) {
            additionSize = MathUtils.randInt(-1, quest.questionStringArray[0].length());
        }
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(mQuestContext,
                quest.questionStringArray[0].length() + additionSize,
                LinearLayoutManager.VERTICAL, false);
        // mViewHolder.textViewGrid.setLayoutManager(gridLayoutManager);
        dataForGrid = new ArrayList<>();
        mTextViews = new ArrayList<>();
        for (String questStringArrayItem : quest.questionStringArray) {
            final char[] charArray = questStringArrayItem.toCharArray();
            for (char aChar : charArray) {
                dataForGrid.add(String.valueOf(aChar).toUpperCase());
                RobotoTextView textView = new RobotoTextView(mQuestContext);
                textView.setText(String.valueOf(aChar).toUpperCase());
                textView.setBackground(mQuestContext.getResources().getDrawable(R.drawable.background_text_camouflage_label));
                textView.setTextColor(mQuestContext.getResources().getColor(R.color.black));
                textView.setGravity(Gravity.CENTER);
                mTextViews.add(textView);
                mTextCamouflageContainer.addView(textView);
            }
        }
        //  mViewHolder.textViewGrid.setAdapter(new TextCamouflageAdapter(dataForGrid));
    }

    @Override
    public void detachView() {
        super.detachView();
        //  mViewHolder.textViewGrid.setLayoutManager(null);
        //  mViewHolder.textViewGrid.setAdapter(null);
    }

    @Override
    public View getView() {
        return mTextCamouflageContainer;
    }

    @Override
    public void updateSize() {
        int width = mRootContentContainer.getWidth();
        int height = mRootContentContainer.getHeight();
        int savedHeight = height;
        int savedWidth = width;
        height = Math.min(width, height);
        Position position = START_POSITION;
        int positionIndex = Position.getIndex(position),
                stepX = 0,
                stepY = 0,
                bounceLeft = 0, bounceBottom = 0;
        boolean isVerticalDirection = (START_POSITION == Position.TL || START_POSITION == Position.BR);
        int textViewWidth = width / mTextViews.size() * 2,
                textViewHeight = height / mTextViews.size() * 2,
                textViewSize = Math.min(textViewWidth, textViewHeight) - 4;
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < mTextViews.size(); i++) {
            TextView textView = mTextViews.get(i);
            if (i != 0) {
                positionIndex = (positionIndex + 1) % Position.values().length == 0 ? 0
                        : positionIndex + 1;
                position = Position.values()[positionIndex];
            }
            if (i != 0) {
                if (START_POSITION == position) {
                    if (isVerticalDirection) {
                        stepY++;
                    } else {
                        stepX++;
                    }
                }
            }
            int x = (position == Position.TL || position == Position.BL) ?
                    textViewWidth * stepX : width - textViewWidth * (stepX + 1);
            int y = (position == Position.TL || position == Position.TR) ?
                    textViewHeight * stepY : height - textViewHeight * (stepY + 1);
            Point point = new Point(x + textViewSize / 2, y + textViewSize / 2);
            pointList.add(point);
            bounceLeft = Math.max(bounceLeft, x + textViewSize);
            bounceBottom = Math.max(bounceBottom, y + textViewSize);
            textView.setY(y);
            textView.setX(x);
            if (i != 0) {
                if (START_POSITION == position) {
                    if (isVerticalDirection) {
                        stepX++;
                    } else {
                        stepY++;
                    }
                }
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewSize / 3);
            ViewGroup.LayoutParams textViewLayoutParams = textView.getLayoutParams();
            textViewLayoutParams.width = textViewSize;
            textViewLayoutParams.height = textViewSize;
        }
        mTextCamouflageContainer.setPointList(pointList);
        mTextCamouflageContainer.setX((savedWidth - bounceLeft) / 2);
        mTextCamouflageContainer.setY((savedHeight - bounceBottom) / 2);
        mTextCamouflageContainer.getLayoutParams().width = width;
        mTextCamouflageContainer.getLayoutParams().height = height;
    }

    private enum Position {
        TL,
        TR,
        BR,
        BL;

        public static int getIndex(Position position) {
            Position[] values = values();
            int index = 0;
            for (Position positionItem : values) {
                if (positionItem == position) {
                    return index;
                }
                index++;
            }
            return index;
        }
    }

    enum VisualType {
        SPIRAL,
        SIN
    }

    private class TextCamouflageContainer extends FrameLayout {

        private Paint paint;
        private List<Point> pointList;

        public TextCamouflageContainer(@NonNull Context context) {
            super(context);
            setWillNotDraw(false);
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
        }

        @Override
        protected void onDetachedFromWindow() {
            paint = null;
            pointList.clear();
            pointList = null;
            super.onDetachedFromWindow();
        }

        public void setPointList(List<Point> pointList) {
            this.pointList = pointList;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    canvas.drawLine(pointList.get(i - 1).x, pointList.get(i - 1).y,
                            pointList.get(i).x,
                            pointList.get(i).y,
                            paint
                    );
                }
            }
        }
    }
}
