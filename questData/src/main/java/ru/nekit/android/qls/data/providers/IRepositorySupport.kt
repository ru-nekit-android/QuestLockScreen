package ru.nekit.android.qls.data.providers

import android.content.SharedPreferences
import io.objectbox.BoxStore
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

interface IRepositorySupport

interface IRepositorySupportBase : IRepositorySupport {

    var repository: IRepositoryHolder
    var boxStore_: BoxStore
}

interface IRepositorySupportExtended : IRepositorySupportBase, IRepositorySupport {

    val sharedPreferences: SharedPreferences
}

