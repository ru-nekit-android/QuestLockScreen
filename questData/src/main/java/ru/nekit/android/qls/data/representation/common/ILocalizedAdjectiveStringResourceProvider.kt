package ru.nekit.android.qls.data.representation.common

import ru.nekit.android.qls.data.providers.IStringResourceProvider
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection

interface ILocalizedAdjectiveStringResourceProvider : IStringResourceProvider {

    val localizedStringResource: LocalizedAdjectiveStringResourceCollection?

}
