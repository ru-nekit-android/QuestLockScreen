package ru.nekit.android.qls.quest.resources.common;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.resources.collections.LocalizedStringResourceCollection;

public interface ILocalizedStringQuestResourceHolder {

    @NonNull
    LocalizedStringResourceCollection getLocalStringResource();

}
