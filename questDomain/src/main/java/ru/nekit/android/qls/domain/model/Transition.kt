package ru.nekit.android.qls.domain.model

enum class Transition {

    INTRODUCTION,
    QUEST,
    LEVEL_UP,
    ADVERT;

    companion object {

        const val EMPTY_TRANSITION = ""

        fun getByName(name: String?): Transition? {
            if (name == EMPTY_TRANSITION || name == null) {
                return null
            }
            return valueOf(name)
        }
    }

    enum class Type {
        CURRENT_TRANSITION,
        PREVIOUS_TRANSITION
    }
}