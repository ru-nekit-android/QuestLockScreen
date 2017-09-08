package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrognito.patternlockview.PatternLockView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class UnlockViewHolder extends ViewHolder {

    PatternLockView patterLockView;

    UnlockViewHolder(@NonNull Context context) {
        super(context, R.layout.wsc_unlock);
        patterLockView = (PatternLockView) getView().findViewById(R.id.unlock_secret_view);
    }

}
