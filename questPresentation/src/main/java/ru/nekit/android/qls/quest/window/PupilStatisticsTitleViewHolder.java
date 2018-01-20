package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class PupilStatisticsTitleViewHolder extends ViewHolder {

    @NonNull
    final AppCompatImageView backgroundImage;
    @NonNull
    final TextView titleTextView, nameTextView;
    @NonNull
    final ViewGroup pupilAvatarMaskContainer, pupilAvatarContainer;

    PupilStatisticsTitleViewHolder(@NonNull Context context) {
        super(context, R.layout.wsc_pupil_statistics_title);
        backgroundImage = (AppCompatImageView) view.findViewById(R.id.view_book_title);
        titleTextView = (TextView) view.findViewById(R.id.tv_title);
        nameTextView = (TextView) view.findViewById(R.id.tv_name);
        pupilAvatarMaskContainer = (ViewGroup) view.findViewById(R.id.container_pupil_avatar_mask);
        pupilAvatarContainer = (ViewGroup) view.findViewById(R.id.container_pupil_avatar);
    }
}
