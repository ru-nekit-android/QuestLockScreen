package ru.nekit.android.qls.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import ru.nekit.android.qls.utils.ViewHolder;
import ru.nekit.android.shared.R;

class WindowViewHolder extends ViewHolder {

    ViewGroup rootContainer, contentContainer;

    WindowViewHolder(@NonNull Context context) {
        super(context, R.layout.layout_window);
        rootContainer = (ViewGroup) view.findViewById(R.id.container_root);
        contentContainer = (ViewGroup) view.findViewById(R.id.container_content);
    }

    public void destroy() {
        rootContainer.removeAllViews();
        contentContainer.removeAllViews();
    }
}
