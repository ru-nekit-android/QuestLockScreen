package ru.nekit.android.qls.quest.view.mediator.answer

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection

//ver 1.0
abstract class SwipeAnswerMediator : AnswerMediator(), ISwipeAnswerMediator, View.OnTouchListener {

    private lateinit var directionDetector: SwipeDirectionDetector
    protected abstract val targetView: View

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        targetView.setOnTouchListener(this)
        directionDetector = object : SwipeDirectionDetector(questContext) {
            override fun onDirectionDetected(direction: SwipeDirectionDetector.Direction) {
                if (SwipeAnswerMediator.SwipeDirectionDetector.Direction.NOT_DETECTED == direction) {
                    answerPublisher.onNext("")
                } else {
                    onSwipe(DirectionResourceCollection.getById(direction.ordinal))
                }
            }
        }
    }

    override fun onSwipe(direction: DirectionResourceCollection) {
        answerPublisher.onNext(direction.ordinal)
    }

    override fun detachView() {
        targetView.setOnTouchListener(null)
        super.detachView()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        directionDetector.onTouchEvent(event)
        return true
    }

    internal abstract class SwipeDirectionDetector(context: Context) {

        private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
        private var startX: Float = 0.toFloat()
        private var startY: Float = 0.toFloat()
        private var isDetected: Boolean = false

        abstract fun onDirectionDetected(direction: Direction)

        fun onTouchEvent(event: MotionEvent) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    if (!isDetected) {
                        onDirectionDetected(Direction.NOT_DETECTED)
                    }
                    startY = 0.0f
                    startX = startY
                    isDetected = false
                }
                MotionEvent.ACTION_MOVE -> if (!isDetected && getDistance(event) > touchSlop) {
                    isDetected = true
                    val x = event.x
                    val y = event.y

                    val direction = getDirection(startX, startY, x, y)
                    onDirectionDetected(direction)
                }
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
        private fun getDirection(x1: Float, y1: Float, x2: Float, y2: Float): Direction {
            val angle = getAngle(x1, y1, x2, y2)
            return Direction[angle]
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
        private fun getAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
            val rad = Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
            return (rad * 180 / Math.PI + 180) % 360
        }

        private fun getDistance(ev: MotionEvent): Float {
            var distanceSum = 0f

            val dx = ev.getX(0) - startX
            val dy = ev.getY(0) - startY
            distanceSum += Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            return distanceSum
        }

        enum class Direction {
            UP,
            RIGHT,
            DOWN,
            LEFT,
            NOT_DETECTED;

            companion object {

                operator fun get(angle: Double): Direction {
                    return if (inRange(angle, 45f, 135f)) {
                        Direction.UP
                    } else if (inRange(angle, 0f, 45f) || inRange(angle, 315f, 360f)) {
                        Direction.RIGHT
                    } else if (inRange(angle, 225f, 315f)) {
                        Direction.DOWN
                    } else {
                        Direction.LEFT
                    }
                }

                private fun inRange(angle: Double, init: Float, end: Float): Boolean {
                    return angle >= init && angle < end
                }
            }
        }
    }
}