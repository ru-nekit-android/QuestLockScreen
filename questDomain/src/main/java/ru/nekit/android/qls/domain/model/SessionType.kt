package ru.nekit.android.qls.domain.model

enum class SessionType(val expiredTime: Long) {

    SETUP_WIZARD(5 * 60 * 1000),
    LOCK_SCREEN(2 * 60 * 1000);

}