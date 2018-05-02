package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.nekit.android.qls.domain.model.LockScreenStartType

@Entity
data class LockScreenStartTypeEntity(@Id
                                     var id: Long = 0,
                                     val timestamp: Long,
                                     @Convert(converter = LockScreenStartTypeConverter::class, dbType = String::class)
                                     var lockScreenStartType: LockScreenStartType)