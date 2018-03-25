package ru.nekit.android.qls.domain.model

//onCreate (view builder) -> attach -> start -> play/pause/resume -> stop
enum class QuestState {

    WAS_RESTORED, //mix
    DELAYED_PLAY, //mix
    ATTACHED,
    STARTED,
    PLAYED,
    PAUSED,
    WAS_STOPPED,//mix
    ANSWERED;
    //mix

    val value: Int
        get() = Math.pow(2.0, ordinal.toDouble()).toInt()

}

