package ru.nekit.android.qls.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ViewSwitcher;

import ru.nekit.android.qls.R;

class PupilStatisticsWindowContentViewHolder extends WindowContentViewHolder {

    @NonNull
    final View bookTitle, bookContent;
    @NonNull
    final ViewSwitcher contentContainer;

    PupilStatisticsWindowContentViewHolder(@NonNull Context context) {
        super(context, R.layout.wc_pupil_statistics);
        contentContainer = (ViewSwitcher) view.findViewById(R.id.container_content);
        bookTitle = view.findViewById(R.id.btn_book_title);
        bookContent = view.findViewById(R.id.btn_book_content);
    }

}