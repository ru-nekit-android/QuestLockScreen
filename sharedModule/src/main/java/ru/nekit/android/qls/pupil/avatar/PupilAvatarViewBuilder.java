package ru.nekit.android.qls.pupil.avatar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.pupil.Pupil;

public class PupilAvatarViewBuilder {

    public static void build(@NonNull Context context, @NonNull Pupil pupil,
                             @NonNull ViewGroup pupilAvatarContainer) {
        List<PupilAvatarPartAndVariant> pupilAvatarPartAndVariants =
                PupilAvatarConverter.toPupilAvatarPartAndVariant(pupil);
        for (PupilAvatarPartAndVariant pupilAvatarPartAndVariant : pupilAvatarPartAndVariants) {
            AppCompatImageView avatarPartImageRepresentation =
                    new AppCompatImageView(context);
            avatarPartImageRepresentation.setImageResource(
                    pupilAvatarPartAndVariant.pupilAvatarPart.getVariant(pupilAvatarPartAndVariant.variant));
            pupilAvatarContainer.addView(avatarPartImageRepresentation);
        }
    }
}
