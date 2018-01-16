package ru.nekit.android.qls.utils;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static ru.nekit.android.shared.R.integer;

public class KeyboardHost {

    public static void hideKeyboard(@NonNull final Context context, @NonNull final View input) {
        hideKeyboard(context, input, null);
    }

    public static void hideKeyboard(@NonNull final Context context, @NonNull final View input, @Nullable final Runnable action) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                IBinder token = input.getWindowToken();
                imm.hideSoftInputFromWindow(token, 0);
                if (action != null) {
                    action.run();
                }
            }
        }, context.getResources().getInteger(integer.hide_keyboard_delay));
    }

    public static void showKeyboard(@NonNull final Context context, @NonNull final View input) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
            }
        }, context.getResources().getInteger(integer.show_keyboard_delay));
    }
}
