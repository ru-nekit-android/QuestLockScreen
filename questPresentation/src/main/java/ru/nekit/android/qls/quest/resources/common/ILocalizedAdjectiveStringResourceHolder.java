package ru.nekit.android.qls.quest.resources.common;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IStringHolder;
import ru.nekit.android.qls.quest.resources.collections.LocalizedAdjectiveStringResourceCollection;

public interface ILocalizedAdjectiveStringResourceHolder extends IStringHolder {

    @NonNull
    LocalizedAdjectiveStringResourceCollection getLocalStringResource();

}
