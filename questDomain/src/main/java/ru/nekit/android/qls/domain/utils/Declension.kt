package ru.nekit.android.qls.domain.utils

import ru.nekit.android.qls.domain.utils.Declension.Gender.*

object Declension {

    fun declineAdjectiveByNoun(adjectiveBase: String,
                               nounBase: String,
                               format: String,
                               gender: Gender,
                               isPlural: Boolean): String {
        return String.format(format, declineAdjective(adjectiveBase, gender, isPlural),
                declineNoun(nounBase, gender, isPlural))
    }

    fun declineNoun(base: String, gender: Gender, isPlural: Boolean): String {
        return base + if (gender == NEUTER) "о" else if (isPlural) "и" else "у"
    }

    private fun declineAdjective(base: String, gender: Gender, isPlural: Boolean): String = base + if (isPlural) {
        when (gender) {
            MALE -> "ые"
            FEMALE -> "ые"
            NEUTER -> "ые"
        }
    } else {
        when (gender) {
            MALE -> "ой"
            FEMALE -> "ую"
            NEUTER -> "ое"
        }
    }

    enum class Gender {
        NEUTER,
        MALE,
        FEMALE
    }

}
