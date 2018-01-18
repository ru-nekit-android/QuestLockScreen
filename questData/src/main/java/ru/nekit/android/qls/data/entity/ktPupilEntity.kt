package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import ru.nekit.android.domain.qls.model.ktComplexity
import ru.nekit.android.domain.qls.model.ktPupilSex

@Entity
data class ktPupilEntity(
        @Id
        var id: Long = 0,
        var uuid: String? = null,
        var name: String? = null,
        @Convert(converter = PupilConverter::class, dbType = String::class)
        var sex: ktPupilSex? = null,
        @Convert(converter = ComplexityConverter::class, dbType = String::class)
        var complexity: ktComplexity? = null,
        val avatar: String? = null
)

open class PupilConverter : PropertyConverter<ktPupilSex, String> {
    override fun convertToEntityProperty(databaseValue: String): ktPupilSex {
        return ktPupilSex.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: ktPupilSex): String {
        return entityProperty.name
    }
}

open class ComplexityConverter : PropertyConverter<ktComplexity, String> {
    override fun convertToEntityProperty(databaseValue: String): ktComplexity {
        return ktComplexity.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: ktComplexity): String {
        return entityProperty.name
    }
}


fun ktPupilSex.asParameter(): String = PupilConverter().convertToDatabaseValue(this)
fun ktComplexity.asParameter(): String = ComplexityConverter().convertToDatabaseValue(this)