package ru.nekit.android.qls.lockScreen.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class WindowViewHolder extends ViewHolder {

    ViewGroup rootContainer, contentContainer;

    WindowViewHolder(@NonNull Context context) {
        super(context, R.layout.layout_window);
        rootContainer = (ViewGroup) view.findViewById(R.id.container_root);
        contentContainer = (ViewGroup) view.findViewById(R.id.container_content);
    }

    public void destroy() {
        rootContainer.setVisibility(View.INVISIBLE);
        rootContainer.removeAllViews();
        contentContainer.removeAllViews();
    }
}
