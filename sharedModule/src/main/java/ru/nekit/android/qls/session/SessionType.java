package ru.nekit.android.qls.session;

public enum SessionType {

    SETUP_WIZARD(60 * 1000),
    LOCK_SCREEN(60 * 1000);

    private long mExpiredTime;

    SessionType(long expiredTime) {
        mExpiredTime = expiredTime;
    }

    public long getExpiredTime() {
        return mExpiredTime;
    }

    public String getName() {
        return name();
    }

}
