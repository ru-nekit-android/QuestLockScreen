package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.PupilSex

@Entity
data class PupilEntity(
        @Id
        var id: Long = 0,
        var uuid: String,
        var name: String? = null,
        @Convert(converter = PupilSexConverter::class, dbType = String::class)
        var sex: PupilSex? = null,
        @Convert(converter = ComplexityConverter::class, dbType = String::class)
        var complexity: Complexity? = null,
        val avatar: String? = null
)

@Entity
data class PhoneContactEntity(@Id var id: Long = 0,
                              var contactId: Long,
                              var pupilId: Long,
                              var name: String,
                              var phoneNumber: String)

@Entity
data class PupilStatisticsEntity(@Id var id: Long = 0,
                                 var pupilId: Long,
                                 var score: Int)

fun PupilSex.asParameter(): String? = PupilSexConverter().convertToDatabaseValue(this)
fun Complexity.asParameter(): String? = ComplexityConverter().convertToDatabaseValue(this)