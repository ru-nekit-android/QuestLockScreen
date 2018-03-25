package ru.nekit.android.qls.shared.model

data class Pupil(
        val uuid: String,
        var name: String? = null,
        var sex: PupilSex? = null,
        var complexity: Complexity? = null,
        var avatar: String? = null
)

enum class PupilSex {

    GIRL,
    BOY;

}

enum class Complexity {

    HARD,
    NORMAL,
    EASY

}