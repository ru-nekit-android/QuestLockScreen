package ru.nekit.android.qls.window;

import android.support.annotation.NonNull;

public interface WindowListener {

    void onWindowOpen(@NonNull Window window);

    void onWindowOpened(@NonNull Window window);

    void onWindowClose(@NonNull Window window);

    void onWindowClosed(@NonNull Window window);
}
