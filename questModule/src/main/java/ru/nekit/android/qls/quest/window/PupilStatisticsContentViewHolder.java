package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class PupilStatisticsContentViewHolder extends ViewHolder {

    @NonNull
    final AppCompatImageView backgroundImage;
    @NonNull
    final TextView contentTextView;
    @NonNull
    final ScrollView scroller;

    PupilStatisticsContentViewHolder(@NonNull Context context) {
        super(context, R.layout.wsc_pupil_statistics_content);
        backgroundImage = (AppCompatImageView) getView().findViewById(R.id.view_book);
        contentTextView = (TextView) getView().findViewById(R.id.tv_content);
        scroller = (ScrollView) getView().findViewById(R.id.container_scroll);
    }
}
