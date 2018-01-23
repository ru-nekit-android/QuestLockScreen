package ru.nekit.android.qls.quest.resources.common;

import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.IStringHolder;
import ru.nekit.android.qls.quest.resources.collections.LocalizedNounStringResourceCollection;

public interface ILocalizedNounStringResourceHolder extends IStringHolder {

    @Nullable
    LocalizedNounStringResourceCollection getLocalStringResource();

}
