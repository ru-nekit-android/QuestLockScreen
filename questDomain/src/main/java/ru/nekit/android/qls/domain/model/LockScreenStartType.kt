package ru.nekit.android.qls.domain.model

enum class LockScreenStartType {

    SETUP_WIZARD,
    SILENCE,
    EXPLICIT,
    ON_SCREEN_OFF,
    ON_BOOT_COMPLETE,
    ON_INCOME_CALL_COMPLETE,
    ON_OUTGOING_CALL_COMPLETE,
    ON_DESTROY,
    ON_NOTIFICATION_CLICK;

    companion object {

        const val NAME = "LockScreenStartType"

        fun getById(id: Int): LockScreenStartType {
            return LockScreenStartType.values()[id]
        }
    }
}