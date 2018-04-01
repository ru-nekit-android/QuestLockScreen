package ru.nekit.android.utils

import android.content.Context
import android.graphics.Point

class RevealPoint : Point() {

    var radius: Float = 0.toFloat()

    companion object {

        const val POSITION_TOP_LEFT = "POSITION_TOP_LEFT"
        const val POSITION_TOP_CENTER = "POSITION_TOP_CENTER"
        const val POSITION_TOP_RIGHT = "POSITION_TOP_RIGHT"

        const val POSITION_MIDDLE_LEFT = "POSITION_MIDDLE_LEFT"
        const val POSITION_MIDDLE_CENTER = "POSITION_MIDDLE_CENTER"
        const val POSITION_MIDDLE_RIGHT = "POSITION_MIDDLE_RIGHT"

        const val POSITION_BOTTOM_LEFT = "POSITION_BOTTOM_LEFT"
        const val POSITION_BOTTOM_CENTER = "POSITION_BOTTOM_CENTER"
        const val POSITION_BOTTOM_RIGHT = "POSITION_BOTTOM_RIGHT"

        fun getRevealPoint(context: Context, position: String): RevealPoint {
            val revealPoint = RevealPoint()
            val screenSize = ScreenHost.getScreenSize(context)
            when (position) {

                POSITION_TOP_LEFT -> {

                    revealPoint.x = 0
                    revealPoint.y = 0
                    revealPoint.radius = Math.hypot(screenSize.x.toDouble(), screenSize.y.toDouble()).toFloat()
                }

                POSITION_TOP_CENTER -> {

                    revealPoint.x = screenSize.x / 2
                    revealPoint.y = 0
                    revealPoint.radius = screenSize.y.toFloat()
                }

                POSITION_TOP_RIGHT -> {

                    revealPoint.x = screenSize.x
                    revealPoint.y = 0
                    revealPoint.radius = Math.hypot(screenSize.x.toDouble(), screenSize.y.toDouble()).toFloat()
                }

                POSITION_MIDDLE_LEFT -> {

                    revealPoint.x = 0
                    revealPoint.y = screenSize.y / 2
                    revealPoint.radius = Math.hypot(screenSize.x.toDouble(), revealPoint.y.toDouble()).toFloat()
                }

                POSITION_MIDDLE_CENTER -> {

                    revealPoint.x = screenSize.x / 2
                    revealPoint.y = screenSize.y / 2
                    revealPoint.radius = Math.hypot(revealPoint.x.toDouble(), revealPoint.y.toDouble()).toFloat()
                }

                POSITION_MIDDLE_RIGHT -> {

                    revealPoint.x = screenSize.x
                    revealPoint.y = screenSize.y / 2
                    revealPoint.radius = Math.hypot(revealPoint.x.toDouble(), revealPoint.y.toDouble()).toFloat()
                }

                POSITION_BOTTOM_LEFT -> {

                    revealPoint.x = 0
                    revealPoint.y = screenSize.y
                    revealPoint.radius = Math.hypot(revealPoint.x.toDouble(), revealPoint.y.toDouble()).toFloat()
                }

                POSITION_BOTTOM_CENTER -> {

                    revealPoint.x = screenSize.x / 2
                    revealPoint.y = screenSize.y
                    revealPoint.radius = screenSize.y.toFloat()
                }

                POSITION_BOTTOM_RIGHT -> {

                    revealPoint.x = screenSize.x
                    revealPoint.y = screenSize.y
                    revealPoint.radius = Math.hypot(screenSize.x.toDouble(), revealPoint.y.toDouble()).toFloat()
                }
            }
            return revealPoint
        }
    }
}
