package ru.nekit.android.qls.quest.mediator.answer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import ru.nekit.android.qls.quest.model.DirectionModel;

public abstract class QuestSwipeAnswerMediator extends AbstractQuestAnswerMediator
        implements ISpecialQuestAnswerMediator, View.OnTouchListener {

    private SwipeDirectionDetector directionDetector;


    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        getTargetView().setOnTouchListener(this);
        directionDetector = new SwipeDirectionDetector(mQuestContext) {
            @Override
            public void onDirectionDetected(Direction direction) {
                if (Direction.NOT_DETECTED.equals(direction)) {
                    mAnswerCallback.emptyAnswer();
                } else {
                    onSwipe(DirectionModel.fromOrdinal(direction.ordinal()));
                }
            }
        };
    }

    @Override
    public void onSwipe(@NonNull DirectionModel direction) {
        if (mAnswerChecker.checkAlternativeInput(mQuest, direction.ordinal())) {
            mAnswerCallback.rightAnswer();
        } else {
            mAnswerCallback.wrongAnswer();
        }
    }

    @Override
    public void detachView() {
        getTargetView().setOnTouchListener(null);
        super.detachView();
    }

    @NonNull
    protected abstract View getTargetView();

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        directionDetector.onTouchEvent(event);
        return true;
    }

    abstract static class SwipeDirectionDetector {

        private int touchSlop;
        private float startX, startY;
        private boolean isDetected;

        SwipeDirectionDetector(Context context) {
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        public abstract void onDirectionDetected(Direction direction);

        void onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (!isDetected) {
                        onDirectionDetected(Direction.NOT_DETECTED);
                    }
                    startX = startY = 0.0f;
                    isDetected = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isDetected && getDistance(event) > touchSlop) {
                        isDetected = true;
                        float x = event.getX();
                        float y = event.getY();

                        Direction direction = getDirection(startX, startY, x, y);
                        onDirectionDetected(direction);
                    }
                    break;
            }
        }

        /**
         * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
         * returns the direction that an arrow pointing from p1 to p2 would have.
         *
         * @param x1 the x position of the first point
         * @param y1 the y position of the first point
         * @param x2 the x position of the second point
         * @param y2 the y position of the second point
         * @return the direction
         */
        public Direction getDirection(float x1, float y1, float x2, float y2) {
            double angle = getAngle(x1, y1, x2, y2);
            return Direction.get(angle);
        }

        /**
         * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
         * The angle is measured with 0/360 being the X-axis to the right, angles
         * increase counter clockwise.
         *
         * @param x1 the x position of the first point
         * @param y1 the y position of the first point
         * @param x2 the x position of the second point
         * @param y2 the y position of the second point
         * @return the angle between two points
         */
        public double getAngle(float x1, float y1, float x2, float y2) {
            double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
            return (rad * 180 / Math.PI + 180) % 360;
        }

        private float getDistance(MotionEvent ev) {
            float distanceSum = 0;

            float dx = (ev.getX(0) - startX);
            float dy = (ev.getY(0) - startY);
            distanceSum += Math.sqrt(dx * dx + dy * dy);

            return distanceSum;
        }

        public enum Direction {
            UP,
            RIGHT,
            DOWN,
            LEFT,
            NOT_DETECTED;

            public static Direction get(double angle) {
                if (inRange(angle, 45, 135)) {
                    return Direction.UP;
                } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                    return Direction.RIGHT;
                } else if (inRange(angle, 225, 315)) {
                    return Direction.DOWN;
                } else {
                    return Direction.LEFT;
                }
            }

            private static boolean inRange(double angle, float init, float end) {
                return (angle >= init) && (angle < end);
            }
        }
    }
}