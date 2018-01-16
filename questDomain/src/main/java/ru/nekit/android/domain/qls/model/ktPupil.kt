package ru.nekit.android.domain.qls.model

data class ktPupil(
        val uuid: String,
        val name: String,
        val sex: ktPupilSex,
        val complexity: ktComplexity,
        var avatar: String
)

enum class ktPupilSex {

    BOY,
    GIRL

}

enum class ktComplexity {

    HARD,
    NORMAL,
    EASY

}