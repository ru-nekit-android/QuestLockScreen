package ru.nekit.android.qls.domain.model.resources.common

import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection

interface IResourceGroupHolder {

    val groups: List<ResourceGroupCollection>

}

