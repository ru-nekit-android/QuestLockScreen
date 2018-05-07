package ru.nekit.android.qls.domain.model

enum class LockScreenStartType {

    SETUP_WIZARD_IN_PROCESS,
    LET_TRY_TO_PLAY,
    LET_PLAY,
    PLAY_NOW,
    ON_SCREEN_OFF,
    ON_BOOT_COMPLETE,
    ON_INCOME_CALL_COMPLETE,
    ON_OUTGOING_CALL_COMPLETE,
    ON_DESTROY,
    ON_NOTIFICATION_CLICK;

    companion object {

        const val NAME = "LockScreenStartType"

        fun getById(id: Int) = LockScreenStartType.values()[id]

    }
}