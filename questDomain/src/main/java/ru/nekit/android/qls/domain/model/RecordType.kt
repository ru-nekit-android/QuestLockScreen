package ru.nekit.android.qls.domain.model

enum class RecordType {

    RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD,
    RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD;

    val value: Int
        get() = Math.pow(2.0, ordinal.toDouble()).toInt()

}