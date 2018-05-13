package ru.nekit.android.qls.dependences

import android.content.Context
import ru.nekit.android.qls.domain.providers.DependenciesHolder

open class ContextDependenciesHolder: DependenciesHolder() {

    lateinit var context:Context

}